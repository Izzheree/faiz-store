package com.faiz0033.faizstore.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.faiz0033.faizstore.domain.model.Laptop
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val laptop: Laptop) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val laptopRepository: LaptopRepository,
    private val laptopId: String
) : ViewModel() {

    val uiState: StateFlow<DetailUiState> = laptopRepository.getLaptopById(laptopId)
        .flatMapLatest { laptop ->
            if (laptop != null) {
                flowOf(DetailUiState.Success(laptop))
            } else {
                flowOf(DetailUiState.Error("Laptop not found"))
            }
        }
        .catch { e ->
            emit(DetailUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailUiState.Loading
        )

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    fun clearDeleteError() {
        _deleteError.value = null
    }

    fun deleteLaptop(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isDeleting.value = true
            _deleteError.value = null
            try {
                laptopRepository.deleteLaptop(laptopId)
                _isDeleting.value = false
                onSuccess()
            } catch (e: Exception) {
                _isDeleting.value = false
                _deleteError.value = e.message ?: "An error occurred while deleting."
            }
        }
    }

    class Factory(
        private val laptopRepository: LaptopRepository,
        private val laptopId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(laptopRepository, laptopId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
