package com.example.save_a_strayv2.repository

import com.example.save_a_strayv2.model.User
import com.example.save_a_strayv2.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    /**
     * Emits the current FirebaseUser whenever Auth state changes.
     */
    val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Registers a new user with Firebase Auth, then immediately saves the profile data to Firestore.
     */
    suspend fun register(
        email: String,
        password: String,
        role: UserRole,
        name: String,
        orgName: String
    ): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Determine names based on role, to match the consolidated roles
                val finalName = if (role == UserRole.INDIVIDUAL) name else ""
                val finalOrgName = if (role == UserRole.SHELTER) orgName else ""

                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    role = role,
                    name = finalName,
                    orgName = finalOrgName,
                    location = null
                )

                // Save to Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()
            }
            AuthResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }

    /**
     * Signs in an existing user with Firebase Auth.
     */
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }

    /**
     * Signs the current user out and clears local state.
     */
    fun logout() {
        auth.signOut()
    }
}
