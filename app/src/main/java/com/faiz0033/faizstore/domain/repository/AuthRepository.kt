package com.faiz0033.faizstore.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isSignedIn: Flow<Boolean>
    val userEmail: Flow<String?>
    val userName: Flow<String?>
    val userProfilePic: Flow<String?>

    suspend fun signInWithGoogle(context: Context): Result<Unit>
    suspend fun signOut()
}
