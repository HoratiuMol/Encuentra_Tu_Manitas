package com.example.encuentratumanitas

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Modelo ───────────────────────────────────────────────────────────────────

@Serializable
data class Job(
    @SerialName("id")          val id: String = "",
    @SerialName("client_id")   val clientId: String = "",
    @SerialName("title")       val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("category")    val category: String = "",
    @SerialName("status")      val status: String = "open",
    @SerialName("location")    val location: String? = null,
    @SerialName("budget")      val budget: Double? = null,
    @SerialName("photo_urls")  val photoUrls: String? = null,
    @SerialName("created_at")  val createdAt: String = ""
)

// Categorías disponibles
enum class JobCategory(val label: String, val emoji: String) {
    FONTANERIA("Fontanería", "🔧"),
    ELECTRICIDAD("Electricidad", "⚡"),
    CARPINTERIA("Carpintería", "🪚"),
    PINTURA("Pintura", "🎨"),
    OTROS("Otros", "🏠")
}

// ─── Repositorio ──────────────────────────────────────────────────────────────

class JobRepository {

    // Obtener trabajos del cliente actual
    suspend fun getMyJobs(): AuthResult<List<Job>> = runCatching {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No hay sesión activa")

        supabaseClient.postgrest["jobs"]
            .select {
                filter { eq("client_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<Job>()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.message ?: "Error al obtener trabajos") }
    )

    // Crear nuevo trabajo
    suspend fun createJob(
        title: String,
        description: String,
        category: String,
        location: String?,
        budget: Double?
    ): AuthResult<Unit> = runCatching {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No hay sesión activa")

        supabaseClient.postgrest["jobs"].insert(
            Job(
                clientId    = userId,
                title       = title,
                description = description,
                category    = category,
                location    = location,
                budget      = budget
            )
        )
    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = { AuthResult.Error(it.message ?: "Error al crear trabajo") }
    )

    // Eliminar trabajo
    suspend fun deleteJob(jobId: String): AuthResult<Unit> = runCatching {
        supabaseClient.postgrest["jobs"]
            .delete { filter { eq("id", jobId) } }
    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = { AuthResult.Error(it.message ?: "Error al eliminar trabajo") }
    )
}
