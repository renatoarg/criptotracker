package com.renatoarg.offlinecriptotracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.renatoarg.offlinecriptotracker.model.CoinDetail
import com.renatoarg.offlinecriptotracker.model.CoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoinDetailViewModel(
    application: Application,
    private val coinId: String
) : AndroidViewModel(application) {

    private val repository = CoinRepository(application.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // observes coin details
    val coinDetail: StateFlow<CoinDetail?> = repository.getCoinDetailFlow(coinId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val uiState: StateFlow<CoinDetailUiState> = combine(
        coinDetail,
        isLoading,
        error
    ) { detail, isLoading, error ->
        CoinDetailUiState(
            coinDetail = detail,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CoinDetailUiState()
    )

    init {
        loadCoinDetail()
    }

    private fun loadCoinDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.refreshCoinDetail(coinId)
                .onSuccess {
                    // Data will be updated automatically via Room Flow
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error loading details"
                }

            _isLoading.value = false
        }
    }

    fun retry() {
        loadCoinDetail()
    }
}

data class CoinDetailUiState(
    val coinDetail: CoinDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)