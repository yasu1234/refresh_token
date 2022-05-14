package com.example.refreshtoken.database

import android.content.Context

class AccountManager(val context: Context) {
    private var sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

    var accessToken: String?
    get() {
        return sharedPreferences.getString("token", null)
    }
    set(value) {
        sharedPreferences.edit().apply {
            putString("token", value)
            apply()
        }
    }
}