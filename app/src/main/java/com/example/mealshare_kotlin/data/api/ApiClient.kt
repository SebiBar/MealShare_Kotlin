package com.example.mealshare_kotlin.data.api

import android.content.Context
import com.example.mealshare_kotlin.data.auth.TokenManager
import com.example.mealshare_kotlin.model.User
import com.example.mealshare_kotlin.model.UserAuthResponse
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ApiClient class for handling Retrofit instance and API service creation
 * Implements application-scoped instance pattern with Hilt
 */
@Singleton
class ApiClient @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val tokenManager: TokenManager
) {
    private val BASEURL = "http://3.70.29.188:8080/api/"
    private val TIMEOUT = 10L

    // Token storage and management
    private var authToken: String? = null
    private var currentUser: User? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Load token from DataStore synchronously to ensure it's available
        runBlocking {
            authToken = tokenManager.getToken().first()
            currentUser = tokenManager.getUser().first()
        }
    }

    /**
     * Set auth data (token and user) and save it to DataStore
     */
    fun setAuthData(authResponse: UserAuthResponse) {
        authToken = authResponse.token
        currentUser = authResponse.user
        coroutineScope.launch {
            tokenManager.saveToken(authResponse.token)
            tokenManager.saveUser(authResponse.user)
        }
    }

    /**
     * Get current user data
     */
    fun getCurrentUser(): User? {
        return currentUser
    }

    /**
     * Clear auth token and user data from memory and DataStore
     */
    fun clearAuthToken() {
        authToken = null
        currentUser = null
        coroutineScope.launch {
            tokenManager.clearToken()
        }
    }

    /**
     * Create OkHttpClient with auth token interceptor
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
            authToken?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Create Retrofit instance
     */
    fun createRetrofitInstance(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

/**
 * Hilt module to provide ApiService dependency
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApiService(apiClient: ApiClient): ApiService {
        return apiClient.createRetrofitInstance().create(ApiService::class.java)
    }
}
