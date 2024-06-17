package com.example.equilibrareapp

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper (context: Context) {
    val IS_LOGGED_IN = "login"
    val PREF_NAME = "user_prefs"
    val KEY_TOKEN = "token"

    val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    fun setStatusLogin(status: Boolean){
        preferences.edit().putBoolean(IS_LOGGED_IN, status).apply()
    }

    fun getStatusLogin(): Boolean{
        return preferences.getBoolean(IS_LOGGED_IN,false)
    }

    fun saveUserToken(token: String){
        preferences.edit().putString(KEY_TOKEN,token).apply()
    }

    fun getUserToken(): String? {
        return preferences.getString(KEY_TOKEN," ")
    }

//    fun clearUserToken(){
//        preferences.edit().remove(KEY_TOKEN).apply()
//    }
//
//    fun clearUserLogin(){
//        preferences.edit().remove(IS_LOGGED_IN).apply()
//    }
    fun clearPref(){
        preferences.edit().clear().apply()
    }
}