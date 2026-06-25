package com.faiz0033.faizstore.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.faiz0033.faizstore.BuildConfig
import com.faiz0033.faizstore.data.local.preferences.AuthPreferences
import com.faiz0033.faizstore.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val credentialManager: CredentialManager,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override val isSignedIn: Flow<Boolean> = authPreferences.isSignedIn
    override val userEmail: Flow<String?> = authPreferences.userEmail
    override val userName: Flow<String?> = authPreferences.userName
    override val userProfilePic: Flow<String?> = authPreferences.userProfilePic

    override suspend fun signInWithGoogle(context: Context): Result<Unit> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                
                val userId = googleIdTokenCredential.id
                val name = googleIdTokenCredential.displayName
                val profilePic = googleIdTokenCredential.profilePictureUri?.toString()
                
                // Decode JWT to get email
                val jwtParts = googleIdTokenCredential.idToken.split(".")
                var email = "user@google.com"
                if (jwtParts.size > 1) {
                    try {
                        val payload = String(android.util.Base64.decode(jwtParts[1], android.util.Base64.DEFAULT))
                        val json = org.json.JSONObject(payload)
                        email = json.optString("email", email)
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to parse JWT", e)
                    }
                }
                
                authPreferences.saveAuthSession(
                    userId = userId, 
                    email = email, 
                    name = name,
                    profilePic = profilePic
                )
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Unexpected credential type"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign in failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("AuthRepository", "Clear credential state failed", e)
        }
        authPreferences.clearAuthSession()
    }
}
