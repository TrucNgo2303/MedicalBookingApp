package com.example.bookingmedicalapp.network

import com.example.bookingmedicalapp.BuildConfig
import com.example.bookingmedicalapp.common.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object ApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            if (Constants.token?.isNotEmpty() == true) {
                requestBuilder.header("Authorization", "Bearer ${Constants.token}")
            }
            requestBuilder.header("lang", "VN")
            requestBuilder.header("channel", "API")

            val requestWithHeaders = requestBuilder.build()
            chain.proceed(requestWithHeaders)
        }
    }
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: APIServices by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(APIServices::class.java)
    }

}