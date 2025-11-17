package com.example.plugd.ui.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val credentialManager = CredentialManager.create(context)

    // Google sign-in SSO
    suspend fun signIn(): Result<GoogleIdTokenCredential> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(
                    com.example.plugd.R.string.web_client_id
                ))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context as Activity
            )

            val credential = response.credential
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential)
            } else {
                Result.failure(Exception("Unexpected credential type"))
            }

        } catch (e: GetCredentialException) {
            Log.e("GoogleAuthUiClient", "Sign-in failed: ${e.localizedMessage}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleAuthUiClient", "Unexpected error: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Firebase sign-in with Google ID token
    suspend fun firebaseSignInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user
    }

    // Sign out
    fun signOut() {
        auth.signOut()
    }

    // Fetch current user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}
