package com.example.encuentratumanitas

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Resultado genérico para manejar éxito / error sin excepciones en la UI
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository {

    // ─── Registro ────────────────────────────────────────────────────────────
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        role: UserRole
    ): AuthResult<Unit> = runCatching {

        val roleString = role.name.lowercase() // "client" | "manitas" | "admin"

        // 1. Crear cuenta en Supabase Auth
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                put("full_name", fullName)
                put("role", roleString)
            }
        }

        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No se pudo obtener el usuario tras el registro")



        // 2. Upsert perfil — el trigger ya lo crea vacío, aquí lo completamos
        supabaseClient.postgrest["profiles"].upsert(
            Profile(
                id       = userId,
                fullName = fullName,
                role     = roleString
            )
        )

        // 3. Upsert en user_roles (compatibilidad con lógica React existente)
        supabaseClient.postgrest["user_roles"].upsert(
            UserRoleRow(userId = userId, role = roleString)
        )

    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = { AuthResult.Error(it.message ?: "Error desconocido en el registro") }
    )

    // ─── Login ───────────────────────────────────────────────────────────────
    suspend fun signIn(email: String, password: String): AuthResult<UserRole> = runCatching {

        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password

        }

        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("Sesión no encontrada tras el login")

        // Consultar rol del usuario
        val roleRow = supabaseClient.postgrest["user_roles"]
            .select(Columns.list("role")) {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<UserRoleRow>()

        when (roleRow?.role) {
            "admin"   -> UserRole.ADMIN
            "manitas" -> UserRole.MANITAS
            else      -> UserRole.CLIENT
        }

    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.message ?: "Error desconocido en el login") }
    )

    // ─── Cerrar sesión ───────────────────────────────────────────────────────
    suspend fun signOut(): AuthResult<Unit> = runCatching {
        supabaseClient.auth.signOut()
    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = { AuthResult.Error(it.message ?: "Error al cerrar sesión") }
    )

    // ─── Sesión actual ───────────────────────────────────────────────────────
    fun isLoggedIn(): Boolean =
        supabaseClient.auth.currentUserOrNull() != null

    suspend fun getCurrentRole(): UserRole? {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            val roleRow = supabaseClient.postgrest["user_roles"]
                .select(Columns.list("role")) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<UserRoleRow>()
            when (roleRow?.role) {
                "admin"   -> UserRole.ADMIN
                "manitas" -> UserRole.MANITAS
                else      -> UserRole.CLIENT
            }
        }.getOrNull()
    }

    // ─── Perfil ──────────────────────────────────────────────────────────────
    suspend fun getProfile(): AuthResult<Profile> = runCatching {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No hay sesión activa")

        supabaseClient.postgrest["profiles"]
            .select { filter { eq("id", userId) } }
            .decodeSingle<Profile>()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.message ?: "Error al obtener perfil") }
    )
}

// ─── Modelos de datos ─────────────────────────────────────────────────────────

@Serializable
data class Profile(
    @SerialName("id")        val id: String = "",
    @SerialName("full_name") val fullName: String = "",  // ← snake_case para Supabase
    @SerialName("role")      val role: String = "client"
)

@Serializable
data class UserRoleRow(
    @SerialName("user_id") val userId: String? = null,   // ← snake_case para Supabase
    @SerialName("role")    val role: String = ""
)

enum class UserRole {
    CLIENT, MANITAS, ADMIN
}