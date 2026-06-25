package com.faiz0033.faizstore.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val laptopRepository: LaptopRepository,
    private val ownerEmail: String
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState: StateFlow<HomeUiState> = laptopRepository.getLaptops(ownerEmail)
        .combine(_searchQuery) { laptops, query ->
            val filteredLaptops = if (query.isBlank()) {
                laptops
            } else {
                laptops.filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    it.brand.contains(query, ignoreCase = true) 
                }
            }
            
            if (filteredLaptops.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Success(filteredLaptops)
            }
        }
        .catch { e ->
            emit(HomeUiState.Error(e.message ?: "An unexpected error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    init {
        syncFull()
    }

    fun sync() {
        viewModelScope.launch {
            laptopRepository.syncUnsyncedLaptops()
        }
    }

    fun syncFull() {
        viewModelScope.launch {
            laptopRepository.syncRemote(ownerEmail)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    class Factory(
        private val laptopRepository: LaptopRepository,
        private val ownerEmail: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(laptopRepository, ownerEmail) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
