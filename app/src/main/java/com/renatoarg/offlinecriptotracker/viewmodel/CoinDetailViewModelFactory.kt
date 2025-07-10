package com.renatoarg.offlinecriptotracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.jvm.java

class CoinDetailViewModelFactory(
    private val coinId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        if (modelClass.isAssignableFrom(CoinDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoinDetailViewModel(application, coinId) as T
        }
        throw kotlin.IllegalArgumentException("Unknown ViewModel class")
    }
}