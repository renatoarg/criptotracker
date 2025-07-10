package com.renatoarg.offlinecriptotracker

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.renatoarg.offlinecriptotracker.model.Coin
import com.renatoarg.offlinecriptotracker.model.api.ApiClient
import com.renatoarg.offlinecriptotracker.model.data.local.DatabaseProvider
import com.renatoarg.offlinecriptotracker.viewmodel.CoinsListViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockApplication = mockk<Application>()
    private val mockDatabase = mockk<com.renatoarg.offlinecriptotracker.model.data.local.AppDatabase>()
    private val mockDao = mockk<com.renatoarg.offlinecriptotracker.model.data.local.CoinMarketDao>()
    private val mockApi = mockk<com.renatoarg.offlinecriptotracker.model.api.CoinApi>()

    private lateinit var viewModel: CoinsListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.isLoggable(any(), any()) } returns false

        // Mock singletons
        mockkObject(DatabaseProvider)
        mockkObject(ApiClient)

        every { mockApplication.applicationContext } returns mockApplication
        every { DatabaseProvider.getDatabase(any()) } returns mockDatabase
        every { mockDatabase.coinMarketDao() } returns mockDao
        every { mockDatabase.coinDetailDao() } returns mockk()
        every { ApiClient.coinApi } returns mockApi

        // Default behavior
        every { mockDao.getAllCoinsFlow() } returns flowOf(emptyList())
        coEvery { mockDao.replaceAllCoins(any()) } just Runs
        coEvery { mockApi.getMarkets(any()) } returns emptyList()

        viewModel = CoinsListViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        val initialState = viewModel.uiState.value
        assertEquals(emptyList<Coin>(), initialState.coins)
        assertFalse(initialState.isRefreshing)
        assertNull(initialState.error)
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        viewModel.clearError()
        assertNull(viewModel.error.value)
    }

    @Test
    fun `refreshCoins should call API`() = runTest {
        // given
        val mockCoins = listOf(
            Coin("any", "any", "any", "any", 50000.0),
            Coin("any2", "any2", "any2", "any2", 3000.0)
        )
        coEvery { mockApi.getMarkets(any()) } returns mockCoins

        // when
        viewModel.refreshCoins()

        // then
        coVerify { mockApi.getMarkets("usd") }
        coVerify { mockDao.replaceAllCoins(any()) }
        assertFalse(viewModel.isRefreshing.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `handle API error`() = runTest {
        // given
        val errorMessage = "Network failed"
        coEvery { mockApi.getMarkets(any()) } throws kotlin.Exception(
            errorMessage
        )

        // when
        viewModel.refreshCoins()

        // then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isRefreshing.value)
        coVerify(exactly = 0) { mockDao.replaceAllCoins(any()) }
    }
    
}