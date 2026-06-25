package com.faiz0033.faizstore.presentation.home

import com.faiz0033.faizstore.domain.model.Laptop

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(val laptops: List<Laptop>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
