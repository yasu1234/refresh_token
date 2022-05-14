package com.example.refreshtoken.session

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ClientManager {
    fun getApiClient(context: Context): ApiClient {
        return Retrofit.Builder()
            .client(getClient(context))
            .build()
            .create(ApiClient::class.java)
    }

    private fun getClient(context: Context): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(context))

        return builder.build()
    }
}