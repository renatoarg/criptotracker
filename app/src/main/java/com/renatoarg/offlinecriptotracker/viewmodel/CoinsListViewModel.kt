package com.renatoarg.offlinecriptotracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.renatoarg.offlinecriptotracker.model.Coin
import com.renatoarg.offlinecriptotracker.model.CoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoinsListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepository(application.applicationContext)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasLoadedFromRoom = MutableStateFlow(false)

    // observes the database
    val coins: StateFlow<List<Coin>> = repository.getCoinMarketsFlow()
        .onEach {
            _hasLoadedFromRoom.value = true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<CoinListUiState> = combine(
        coins,
        isRefreshing,
        error,
        _hasLoadedFromRoom
    ) { coins, isRefreshing, error, hasLoadedFromRoom ->
        CoinListUiState(
            coins = coins,
            isRefreshing = isRefreshing,
            error = error,
            hasLoadedFromRoom = hasLoadedFromRoom
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CoinListUiState()
    )

    fun refreshCoins() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null

            repository.refreshCoinMarkets()
                .onSuccess {
                    // Data will be updated automatically via Room Flow
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error updating coins"
                }

            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

data class CoinListUiState(
    val coins: List<Coin> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasLoadedFromRoom: Boolean = false
)