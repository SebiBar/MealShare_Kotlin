package com.example.mealshare_kotlin.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mealshare_kotlin.model.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TokenManager handles storing and retrieving JWT tokens and user data using DataStore
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
    }

    private val gson = Gson()

    /**
     * Save JWT token to DataStore
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    /**
     * Get JWT token as a Flow
     */
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }
    }

    /**
     * Save user data to DataStore
     */
    suspend fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        context.dataStore.edit { preferences ->
            preferences[USER_DATA_KEY] = userJson
        }
    }

    /**
     * Get user data as a Flow
     */
    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_DATA_KEY]?.let { userJson ->
                try {
                    gson.fromJson(userJson, User::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    /**
     * Get user ID as a Flow
     */
    fun getUserId(): Flow<Long?> {
        return getUser().map { user ->
            user?.id
        }
    }

    /**
     * Clear JWT token and user data from DataStore
     */
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
            preferences.remove(USER_DATA_KEY)
        }
    }
}
