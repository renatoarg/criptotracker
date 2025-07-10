@file:OptIn(ExperimentalMaterial3Api::class)

package com.renatoarg.offlinecriptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.renatoarg.offlinecriptotracker.view.CoinDetailScreen
import com.renatoarg.offlinecriptotracker.view.CoinsListScreen
import com.renatoarg.offlinecriptotracker.view.NavigationIcon
import com.renatoarg.offlinecriptotracker.view.ui.theme.CryptoTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerTheme {
                var coinId by remember { mutableStateOf<String?>(null) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { if (coinId == null) { Text("Crypto Tracker") } },
                            navigationIcon = { if (coinId != null) { NavigationIcon { coinId = null }} },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (coinId == null) {
                            CoinsListScreen( onClick = { coinId = it} )
                        } else {
                            CoinDetailScreen( id = coinId!!, onBackPressed = { coinId == null } )
                        }
                    }
                }
            }
        }
    }
}