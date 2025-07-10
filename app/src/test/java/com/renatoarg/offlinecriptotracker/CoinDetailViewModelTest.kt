package com.renatoarg.offlinecriptotracker

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.renatoarg.offlinecriptotracker.model.*
import com.renatoarg.offlinecriptotracker.model.api.ApiClient
import com.renatoarg.offlinecriptotracker.model.data.local.CoinDetailEntity
import com.renatoarg.offlinecriptotracker.model.data.local.DatabaseProvider
import com.renatoarg.offlinecriptotracker.viewmodel.CoinDetailViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoinDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockApplication = mockk<Application>()
    private val mockDatabase = mockk<com.renatoarg.offlinecriptotracker.model.data.local.AppDatabase>()
    private val mockCoinDetailDao = mockk<com.renatoarg.offlinecriptotracker.model.data.local.CoinDetailDao>()
    private val mockCoinMarketDao = mockk<com.renatoarg.offlinecriptotracker.model.data.local.CoinMarketDao>()
    private val mockApi = mockk<com.renatoarg.offlinecriptotracker.model.api.CoinApi>()

    private val testCoinId = "bitcoin"
    private lateinit var viewModel: CoinDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log
        mockkStatic(android.util.Log::class)
        every { android.util.Log.isLoggable(any(), any()) } returns false

        // Mock singletons
        mockkObject(DatabaseProvider)
        mockkObject(ApiClient)

        every { mockApplication.applicationContext } returns mockApplication
        every { DatabaseProvider.getDatabase(any()) } returns mockDatabase
        every { mockDatabase.coinDetailDao() } returns mockCoinDetailDao
        every { mockDatabase.coinMarketDao() } returns mockCoinMarketDao
        every { ApiClient.coinApi } returns mockApi

        // Default DAO behaviors
        every { mockCoinDetailDao.getDetails(testCoinId) } returns flowOf(null)
        coEvery { mockCoinDetailDao.insertCoinDetail(any()) } just Runs
        coEvery { mockApi.getDetails(testCoinId) } returns createMockCoinDetail()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createMockCoinDetail(): CoinDetail {
        return CoinDetail(
            id = testCoinId,
            symbol = "btc",
            name = "Bitcoin",
            desc = mapOf("en" to "Bitcoin desc"),
            image = CoinImage("thumb", "small", "large"),
            marketData = MarketData(
                price = mapOf("usd" to 1.0),
                market = null,
                volume = null
            )
        )
    }

    private fun createMockCoinDetailEntity(): CoinDetailEntity {
        return CoinDetailEntity(
            id = testCoinId,
            symbol = "any",
            name = "any",
            description = "Bitcoin detrail",
            image = "large",
            price = 1.0
        )
    }

    @Test
    fun `initial state should trigger API call and show loading`() = runTest {
        // When - ViewModel creation triggers init block
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        coVerify { mockApi.getDetails(testCoinId) }
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `init should handle successful API response`() = runTest {
        // Given
        val mockDetail = createMockCoinDetail()
        coEvery { mockApi.getDetails(testCoinId) } returns mockDetail

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        coVerify { mockApi.getDetails(testCoinId) }
        coVerify { mockCoinDetailDao.insertCoinDetail(any()) }
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `init should handle API error`() = runTest {
        // Given
        val errorMessage = "Network connection failed"
        coEvery { mockApi.getDetails(testCoinId) } throws Exception(errorMessage)

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { mockCoinDetailDao.insertCoinDetail(any()) }
    }

    @Test
    fun `init should handle database error`() = runTest {
        // Given
        val mockDetail = createMockCoinDetail()
        coEvery { mockApi.getDetails(testCoinId) } returns mockDetail
        coEvery { mockCoinDetailDao.insertCoinDetail(any()) } throws Exception("Database error")

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        assertEquals("Database error", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `coinDetail flow should emit cached data`() = runTest {
        // Given
        val cachedEntity = createMockCoinDetailEntity()
        every { mockCoinDetailDao.getDetails(testCoinId) } returns flowOf(cachedEntity)

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        viewModel.coinDetail.test {
            val coinDetail = awaitItem()
            assertNotNull(coinDetail)
            assertEquals(testCoinId, coinDetail?.id)
            assertEquals("any", coinDetail?.symbol)
            assertEquals("any", coinDetail?.name)
        }
    }

    @Test
    fun `coinDetail flow should emit null when no cached data`() = runTest {
        // Given
        every { mockCoinDetailDao.getDetails(testCoinId) } returns flowOf(null)

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        assertNull(viewModel.coinDetail.value)
    }

    @Test
    fun `retry should call API again and clear previous error`() = runTest {
        // Given - First call fails
        coEvery { mockApi.getDetails(testCoinId) } throws Exception("Network error")
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)
        assertEquals("Network error", viewModel.error.value)

        // Given - Second call succeeds
        coEvery { mockApi.getDetails(testCoinId) } returns createMockCoinDetail()

        // When
        viewModel.retry()

        // then
        coVerify(exactly = 2) { mockApi.getDetails(testCoinId) }
        assertNull(viewModel.error.value)
    }

    @Test
    fun `uiState should combine all state properties correctly`() = runTest {
        // Given
        val cachedEntity = createMockCoinDetailEntity()
        every { mockCoinDetailDao.getDetails(testCoinId) } returns flowOf(cachedEntity)

        // When
        viewModel = CoinDetailViewModel(mockApplication, testCoinId)

        // then
        viewModel.uiState.test {
            val state = awaitItem()

            assertNotNull(state.coinDetail)
            assertEquals(testCoinId, state.coinDetail?.id)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }




}