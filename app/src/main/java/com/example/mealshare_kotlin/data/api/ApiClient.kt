package com.example.mealshare_kotlin.data.api

import android.content.Context
import com.example.mealshare_kotlin.data.auth.TokenManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiClient class for handling Retrofit instance and API service creation
 * Uses application-scoped instance pattern instead of singleton to avoid memory leaks
 */
class ApiClient private constructor(applicationContext: Context) {
    private val BASEURL = "http://3.70.29.188:8080/api/"
    private val TIMEOUT = 10L

    // Token storage and management
    private var authToken: String? = null
    private val tokenManager: TokenManager = TokenManager(applicationContext)
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Load token from DataStore if available
        coroutineScope.launch {
            authToken = tokenManager.getToken().first()
        }
    }

    /**
     * Set auth token and save it to DataStore
     */
    fun setAuthToken(token: String) {
        authToken = token
        coroutineScope.launch {
            tokenManager.saveToken(token)
        }
    }

    /**
     * Clear auth token from memory and DataStore
     */
    fun clearAuthToken() {
        authToken = null
        coroutineScope.launch {
            tokenManager.deleteToken()
        }
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return !authToken.isNullOrEmpty()
    }

    // For synchronous token retrieval in interceptor
    private fun getTokenSynchronously(): String? {
        return try {
            runBlocking { tokenManager.getToken().first() }
        } catch (e: Exception) {
            null
        }
    }

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Authentication interceptor
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()

        // If token not in memory but available in DataStore, get it
        if (authToken == null) {
            authToken = getTokenSynchronously()
        }

        // Only add auth header if token is available
        val requestBuilder = if (!authToken.isNullOrEmpty()) {
            original.newBuilder()
                .header("Authorization", "Bearer $authToken")
        } else {
            original.newBuilder()
        }

        val request = requestBuilder
            .method(original.method, original.body)
            .build()

        chain.proceed(request)
    }

    // Create OkHttpClient with logging for debug builds
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(authInterceptor) // Add auth interceptor
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Create API service
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    companion object {
        @Volatile
        private var instance: ApiClient? = null

        fun getInstance(context: Context): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient(context.applicationContext).also { instance = it }
            }
        }
    }
}
