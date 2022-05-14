package com.example.refreshtoken.session

import android.content.Context
import com.example.refreshtoken.database.AccountManager

object TokenManager {
    var isRefreshing = false

    fun getAccountManager(context: Context): AccountManager {
        return AccountManager(context)
    }
}