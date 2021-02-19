package com.example.bookbnb

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.auth0.android.jwt.JWT
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var userNotLoggedIn: Boolean = true
    private val FIREBASE_TAG = "Firebase Realtime DB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else{
            createFirebaseUser(sessionManager.getUserId()!!)
            if (sessionManager.isUserHost()){
                val intent = Intent(this, AnfitrionActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, HuespedActivity::class.java)
                startActivity(intent)
            }
        }
        finish()
    }

    fun createFirebaseUser(userId: String){
        val sessionMgr = SessionManager(this)
        lifecycleScope.launch {
            when (val userResponse = BookBnBApi(application).getUser(userId)) {
                /*is ResultWrapper.NetworkError -> showSnackbarErrorMessage(getApplication<Application>().getString(R.string.network_error_msg))
                is ResultWrapper.GenericError -> showGenericError(loginResponse)*/
                is ResultWrapper.Success -> {
                    val firebaseService = FirebaseDBService()
                    val user: User = userResponse.value
                    sessionMgr.saveUserFullName(user.getFullName())
                    firebaseService.createUserIfNotExists(
                        userId,
                        user.getFullName(),
                        user.email,
                        { Log.d(FIREBASE_TAG, "Se creó el usuario")},
                        { Log.d(FIREBASE_TAG, "Error creando el usuario") })
                }
            }
        }
    }
}