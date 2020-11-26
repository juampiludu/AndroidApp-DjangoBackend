package com.luduena.djangobackend

import android.content.Context


object SharedDataGetSet {
    fun getMySavedToken(context: Context): String {
        val preferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val tokenInFunc = preferences.getString("token", "")
        return "Token $tokenInFunc"
    }
}