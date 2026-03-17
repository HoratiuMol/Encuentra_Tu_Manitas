package com.example.encuentratumanitas

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Modelo extendido de perfil ───────────────────────────────────────────────
@Serializable
data class FullProfile(
    @SerialName("id")           val id: String = "",
    @SerialName("full_name")    val fullName: String = "",
    @SerialName("avatar_url")   val avatarUrl: String? = null,
    @SerialName("phone")        val phone: String? = null,
    @SerialName("role")         val role: String = "client",
    @SerialName("bio")          val bio: String? = null,
    @SerialName("specialties")  val specialties: String? = null,
    @SerialName("city")         val city: String? = null,
    @SerialName("rating")       val rating: Double = 0.0,
    @SerialName("created_at")   val createdAt: String = ""
)

// ─── Estados UI ───────────────────────────────────────────────────────────────
data class ProfileUiState(
    val profile: FullProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

// ─── ViewModel ────────────────────────────────────────────────────────────────
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching {
                val userId = supabaseClient.auth.currentUserOrNull()?.id
                    ?: throw Exception("No hay sesión activa")
                supabaseClient.postgrest["profiles"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<FullProfile>()
            }.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, profile = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                supabaseClient.postgrest.rpc("delete_own_account")
                supabaseClient.auth.signOut()  // ← asegúrate que esta línea existe
            }.fold(
                onSuccess = { onDone() },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message) }
            )
        }
    }

    fun saveProfile(
        fullName: String,
        city: String,
        bio: String,
        specialties: String,
        avatarUri: Uri?,
        context: android.content.Context
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)
            runCatching {
                val userId = supabaseClient.auth.currentUserOrNull()?.id
                    ?: throw Exception("No hay sesión activa")

                // 1. Subir foto si hay una nueva seleccionada
                var avatarUrl = _uiState.value.profile?.avatarUrl
                if (avatarUri != null) {
                    avatarUrl = uploadAvatar(userId, avatarUri, context)
                }

                // 2. Actualizar perfil en Supabase
                val current = _uiState.value.profile ?: FullProfile(id = userId)
                supabaseClient.postgrest["profiles"].upsert(
                    FullProfile(
                        id          = userId,
                        fullName    = fullName.trim(),
                        avatarUrl   = avatarUrl,
                        phone       = current.phone,
                        role        = current.role,
                        bio         = bio.trim().ifBlank { null },
                        specialties = specialties.trim().ifBlank { null },
                        city        = city.trim().ifBlank { null },
                        rating      = current.rating
                    )
                )
            }.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                    loadProfile()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isSaving = false, error = it.message)
                }
            )
        }
    }

    private suspend fun uploadAvatar(
        userId: String,
        uri: Uri,
        context: android.content.Context
    ): String {
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            ?: throw Exception("No se pudo leer la imagen")
        val path = "avatars/$userId.jpg"
        supabaseClient.storage["avatars"].upload(path, bytes) {
            upsert = true
        }
        return supabaseClient.storage["avatars"].publicUrl(path)
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}