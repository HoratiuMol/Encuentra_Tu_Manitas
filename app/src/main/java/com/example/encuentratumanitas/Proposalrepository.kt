package com.example.encuentratumanitas

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Modelo propuesta ─────────────────────────────────────────────────────────
@Serializable
data class Proposal(
    @SerialName("id")          val id: String = "",
    @SerialName("job_id")      val jobId: String = "",
    @SerialName("manitas_id")  val manitasId: String = "",
    @SerialName("message")     val message: String = "",
    @SerialName("price")       val price: Double? = null,
    @SerialName("status")      val status: String = "pending",
    @SerialName("created_at")  val createdAt: String = ""
)

// ─── Repositorio ──────────────────────────────────────────────────────────────
class ProposalRepository {

    // Todos los trabajos abiertos (para el manitas)
    suspend fun getAvailableJobs(category: String? = null): AuthResult<List<Job>> = runCatching {
        supabaseClient.postgrest["jobs"]
            .select {
                filter {
                    eq("status", "open")
                    if (category != null) eq("category", category)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Job>()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.message ?: "Error al obtener trabajos") }
    )

    // Propuestas enviadas por este manitas
    suspend fun getMyProposals(): AuthResult<List<Proposal>> = runCatching {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No hay sesión activa")

        supabaseClient.postgrest["proposals"]
            .select {
                filter { eq("manitas_id", userId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Proposal>()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Error(it.message ?: "Error al obtener propuestas") }
    )

    // Enviar propuesta
    suspend fun sendProposal(
        jobId: String,
        message: String,
        price: Double?
    ): AuthResult<Unit> = runCatching {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("No hay sesión activa")

        supabaseClient.postgrest["proposals"].insert(
            Proposal(
                jobId     = jobId,
                manitasId = userId,
                message   = message,
                price     = price
            )
        )
    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = {
            val msg = it.message ?: "Error al enviar propuesta"
            // Detectar duplicado (unique constraint)
            if (msg.contains("unique", ignoreCase = true) || msg.contains("duplicate", ignoreCase = true))
                AuthResult.Error("Ya has enviado una propuesta para este trabajo")
            else
                AuthResult.Error(msg)
        }
    )
}