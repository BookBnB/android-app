package com.example.bookbnb.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.example.bookbnb.LoginActivity
import com.example.bookbnb.R
import com.example.bookbnb.network.MyFirebaseMessagingService

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_FULL_NAME = "user_full_name"
    }

    fun logout(activity: Activity){
        removeAuthToken()
        MyFirebaseMessagingService().disableFCM() // Disables notification token
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }

    fun removeAuthToken(){
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): String?{
        val token = fetchAuthToken() ?: return null
        val jwtToken = JWT(token)
        return jwtToken.getClaim("id").asString()
    }

    fun getUserEmail(): String?{
        val token = fetchAuthToken() ?: return null
        val jwtToken = JWT(token)
        return jwtToken.getClaim("email").asString()
    }

    fun getUserRole(): String?{
        val token = fetchAuthToken() ?: return null
        val jwtToken = JWT(token)
        return jwtToken.getClaim("role").asString()
    }

    fun saveUserFullName(name: String) {
        val editor = prefs.edit()
        editor.putString(USER_FULL_NAME, name)
        editor.apply()
    }

    fun getUserFullName() : String?{
        return prefs.getString(USER_FULL_NAME, null)
    }

    fun isUserHost() : Boolean{
        return getUserRole() == "host"
    }
}