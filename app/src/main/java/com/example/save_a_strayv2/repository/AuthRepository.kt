package com.example.save_a_strayv2.repository

import com.example.save_a_strayv2.model.User
import com.example.save_a_strayv2.model.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    /**
     * Emits the current UserInfo whenever Auth state changes.
     */
    val currentUserFlow: Flow<UserInfo?> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session.user
            else -> null
        }
    }

    val currentUser: UserInfo?
        get() = supabase.auth.currentUserOrNull()

    /**
     * Registers a new user with Supabase Auth, then immediately saves the profile data to Postgrest "users" table.
     */
    suspend fun register(
        email: String,
        password: String,
        role: UserRole,
        name: String,
        orgName: String
    ): AuthResult {
        return try {
            // 1. Create Auth Account
            val response = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // In Supabase 3.0, the ID is often inside the user object of the response
            val userId = response?.id ?: supabase.auth.currentUserOrNull()?.id

            println("DEBUG: New User ID extracted: $userId")

            if (userId != null) {
                try {
                    val finalName = if (role == UserRole.INDIVIDUAL) name else orgName

                    // We must map 'uid' to 'id' to match the Postgres column
                    // I am using a Map here to be 100% sure the column names match Postgres
                    val userMap = mapOf(
                        "id" to userId, // Matches your 'users' table column
                        "email" to email,
                        "role" to role.name, // Sends "INDIVIDUAL" or "SHELTER"
                        "name" to finalName
                    )

                    supabase.postgrest["users"].insert(userMap)

                    println("DEBUG: Profile insert for $userId SUCCESSFUL")
                    return AuthResult.Success
                } catch (dbError: Exception) {
                    dbError.printStackTrace()
                    return AuthResult.Error("Auth ok, but profile failed: ${dbError.message}")
                }
            } else {
                return AuthResult.Error("Sign-up succeeded, but UID is null.")
            }
        } catch (authError: Exception) {
            authError.printStackTrace()
            return AuthResult.Error(authError.localizedMessage ?: "Registration failed")
        }
    }

    /**
     * Signs in an existing user with Supabase Auth.
     */
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }

    /**
     * Signs the current user out and clears local state.
     */
    suspend fun logout() {
        supabase.auth.signOut()
    }
}
