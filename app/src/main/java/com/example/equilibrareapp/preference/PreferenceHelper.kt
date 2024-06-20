package com.example.equilibrareapp.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.equilibrareapp.model.User
import com.google.gson.Gson

class PreferenceHelper(context: Context) {
    private val PREF_NAME = "user_prefs"
    private val KEY_TOKEN = "token"
    private val KEY_USER = "user"
    private val IS_LOGGED_IN = "login"
    private val UID = "UID"
    private val KEY_LAST_FETCH_DATE = "last_fetch_date"

    val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status: Boolean) {
        preferences.edit().putBoolean(IS_LOGGED_IN, status).apply()
    }

    fun getStatusLogin(): Boolean {
        return preferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun saveUserToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getUserToken(): String? {
        return preferences.getString(KEY_TOKEN, " ")
    }

    fun saveUserUid(uid: String) {
        preferences.edit().putString(UID, uid).apply()
    }

    fun getUserUid(): String? {
        return preferences.getString(UID, "")
    }

    fun saveLastFetchDate(date: String) {
        preferences.edit().putString(KEY_LAST_FETCH_DATE, date).apply()
    }

    fun getLastFetchDate(): String? {
        return preferences.getString(KEY_LAST_FETCH_DATE, null)
    }

    fun clearPref() {
        preferences.edit().clear().apply()
    }

    fun clearUserData() {
        preferences.edit().remove(KEY_USER).apply()
    }
}
