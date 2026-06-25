package com.faiz0033.faizstore.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    companion object {
        private val IS_SIGNED_IN = booleanPreferencesKey("is_signed_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PROFILE_PIC = stringPreferencesKey("user_profile_pic")
    }

    val isSignedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SIGNED_IN] ?: false
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val userProfilePic: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_PROFILE_PIC]
    }

    suspend fun saveAuthSession(userId: String, email: String, name: String?, profilePic: String?) {
        context.dataStore.edit { preferences ->
            preferences[IS_SIGNED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            if (name != null) {
                preferences[USER_NAME] = name
            }
            if (profilePic != null) {
                preferences[USER_PROFILE_PIC] = profilePic
            }
        }
    }

    suspend fun clearAuthSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
