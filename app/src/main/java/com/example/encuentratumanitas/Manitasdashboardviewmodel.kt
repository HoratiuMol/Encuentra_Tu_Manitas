package com.example.encuentratumanitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Estados ──────────────────────────────────────────────────────────────────

data class ManitasUiState(
    val profile: Profile? = null,
    val availableJobs: List<Job> = emptyList(),
    val myProposals: List<Proposal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SendProposalState {
    object Idle : SendProposalState()
    object Loading : SendProposalState()
    object Success : SendProposalState()
    data class Error(val message: String) : SendProposalState()
}

// ─── ViewModel ────────────────────────────────────────────────────────────────
class ManitasDashboardViewModel(
    private val proposalRepository: ProposalRepository = ProposalRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManitasUiState())
    val uiState: StateFlow<ManitasUiState> = _uiState.asStateFlow()

    private val _sendProposalState = MutableStateFlow<SendProposalState>(SendProposalState.Idle)
    val sendProposalState: StateFlow<SendProposalState> = _sendProposalState.asStateFlow()

    // Categoría seleccionada para filtrar (null = todas)
    private var selectedCategory: String? = null

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val profileResult   = authRepository.getProfile()
            val jobsResult      = proposalRepository.getAvailableJobs(selectedCategory)
            val proposalsResult = proposalRepository.getMyProposals()

            _uiState.value = _uiState.value.copy(
                isLoading     = false,
                profile       = (profileResult as? AuthResult.Success)?.data,
                availableJobs = (jobsResult as? AuthResult.Success)?.data ?: emptyList(),
                myProposals   = (proposalsResult as? AuthResult.Success)?.data ?: emptyList(),
                error         = (jobsResult as? AuthResult.Error)?.message
            )
        }
    }

    fun filterByCategory(category: String?) {
        selectedCategory = category
        loadData()
    }

    fun sendProposal(jobId: String, message: String, price: Double?) {
        if (message.isBlank()) {
            _sendProposalState.value = SendProposalState.Error("El mensaje es obligatorio")
            return
        }
        viewModelScope.launch {
            _sendProposalState.value = SendProposalState.Loading
            when (val result = proposalRepository.sendProposal(jobId, message, price)) {
                is AuthResult.Success -> {
                    _sendProposalState.value = SendProposalState.Success
                    loadData() // recargar para reflejar propuesta enviada
                }
                is AuthResult.Error -> _sendProposalState.value = SendProposalState.Error(result.message)
            }
        }
    }

    fun resetSendProposalState() {
        _sendProposalState.value = SendProposalState.Idle
    }

    // Comprobar si el manitas ya envió propuesta para un trabajo
    fun hasProposalFor(jobId: String): Boolean =
        _uiState.value.myProposals.any { it.jobId == jobId }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }
}