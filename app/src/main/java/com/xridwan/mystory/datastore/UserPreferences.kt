package com.xridwan.mystory.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class UserPreferences private constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[KEY_TOKEN] ?: ""
        }
    }

    fun isLogin(): Flow<Boolean> {
        return dataStore.data.map {
            it[KEY_STATE] ?: false
        }
    }

    suspend fun setToken(token: String) {
        dataStore.edit {
            it[KEY_TOKEN] = token
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[KEY_STATE] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[KEY_STATE] = false
            preferences[KEY_TOKEN] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        val KEY_STATE = booleanPreferencesKey("state")
        val KEY_TOKEN = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}