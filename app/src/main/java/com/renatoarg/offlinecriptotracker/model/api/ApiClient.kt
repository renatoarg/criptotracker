package com.renatoarg.offlinecriptotracker.model.api

import com.renatoarg.offlinecriptotracker.BuildConfig
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createApiKeyInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val coinApi: CoinApi by lazy {
        retrofit.create(CoinApi::class.java)
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private fun createApiKeyInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.request()
            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter("x_cg_demo_api_key", BuildConfig.API_KEY)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()
            it.proceed(newRequest)
        }
    }
}