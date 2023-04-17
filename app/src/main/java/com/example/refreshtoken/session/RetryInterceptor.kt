package com.example.refreshtoken.session

import android.content.Context
import com.example.refreshtoken.database.AccountManager
import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor(val context: Context): Interceptor {
    enum class ResponseCode(val status: Int) {
        SHOULDREFRESH(403)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder().build()
        // temporary data for check whether refresh
        var accessToken = TokenManager.getAccountManager(context).accessToken

        if (!AccountManager(context).accessToken.isNullOrEmpty()) {
            // add header (accessToken)
            request = chain.request()
                .newBuilder()
                .addHeader("HEADER", AccountManager(context).accessToken!!).build()
        }

        var response = chain.proceed(request)

        val retryMaxCount = 2
        var retryCount = 0

        while (!response.isSuccessful && retryCount < retryMaxCount) {
            // not to check until finish refreshing
            if (TokenManager.isRefreshing) {
                continue
            }
            retryCount++

            val lastAccessToken = TokenManager.getAccountManager(context).accessToken

            when (response.code) {
                ResponseCode.SHOULDREFRESH.status -> {
                    if (!lastAccessToken.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                        accessToken = lastAccessToken

                        response.close()

                        response = chain.proceed(
                            request.newBuilder()
                            .removeHeader("HEADER").addHeader("", accessToken).build())
                    } else {
                        if (!TokenManager.isRefreshing) {
                            TokenManager.isRefreshing = true

                            val updateToken = updateToken()
                            TokenManager.isRefreshing = false

                            TokenManager.getAccountManager(context).accessToken = updateToken
                        }
                    }
                }
                else -> {
                    accessToken = lastAccessToken

                    response.close()

                    response = chain.proceed(
                        request.newBuilder()
                            .removeHeader("HEADER").addHeader("HEADER", accessToken!!).build())
                }
            }

            if (retryCount > retryMaxCount) {
                // error handling
            }
        }

        return response
    }

    private fun updateToken(): String {
        // update token here (e.g api)

        return ""
    }
}
