package com.example.encuentratumanitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Estados ──────────────────────────────────────────────────────────────────

data class ClientDashboardUiState(
    val profile: Profile? = null,
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val jobCreated: Boolean = false
)

sealed class CreateJobState {
    object Idle : CreateJobState()
    object Loading : CreateJobState()
    object Success : CreateJobState()
    data class Error(val message: String) : CreateJobState()
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

class ClientDashboardViewModel(
    private val jobRepository: JobRepository = JobRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDashboardUiState())
    val uiState: StateFlow<ClientDashboardUiState> = _uiState.asStateFlow()

    private val _createJobState = MutableStateFlow<CreateJobState>(CreateJobState.Idle)
    val createJobState: StateFlow<CreateJobState> = _createJobState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Cargar perfil y trabajos en paralelo
            val profileResult = authRepository.getProfile()
            val jobsResult    = jobRepository.getMyJobs()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                profile   = (profileResult as? AuthResult.Success)?.data,
                jobs      = (jobsResult as? AuthResult.Success)?.data ?: emptyList(),
                error     = (profileResult as? AuthResult.Error)?.message
                    ?: (jobsResult as? AuthResult.Error)?.message
            )
        }
    }

    fun createJob(
        title: String,
        description: String,
        category: String,
        location: String?,
        budget: Double?
    ) {
        if (title.isBlank()) { _createJobState.value = CreateJobState.Error("El título es obligatorio"); return }
        if (description.isBlank()) { _createJobState.value = CreateJobState.Error("La descripción es obligatoria"); return }

        viewModelScope.launch {
            _createJobState.value = CreateJobState.Loading
            when (val result = jobRepository.createJob(title, description, category, location, budget)) {
                is AuthResult.Success -> {
                    _createJobState.value = CreateJobState.Success
                    loadData() // recargar lista
                }
                is AuthResult.Error -> _createJobState.value = CreateJobState.Error(result.message)
            }
        }
    }

    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            jobRepository.deleteJob(jobId)
            loadData()
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }

    fun resetCreateJobState() {
        _createJobState.value = CreateJobState.Idle
    }
}