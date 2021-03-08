package com.example.bookbnb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.network.MyFirebaseMessagingService
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.utils.SessionManager
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var userNotLoggedIn: Boolean = true
    private val FIREBASE_TAG = "Firebase Realtime DB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()

        if (intent.hasExtra("deeplink")){
            intent.data = Uri.parse(intent.extras!!.getString("deeplink"))
        }
        if (intent.data != null){
            Log.e("ASD", intent.data.toString())
        }

        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else{
            createFirebaseUser(sessionManager.getUserId()!!)
            MyFirebaseMessagingService().enableFCM() // Enables notification token
            if (sessionManager.isUserHost()){
                val newIntent = Intent(this, AnfitrionActivity::class.java)
                if (intent.data != null) {
                    newIntent.data = intent.data
                }
                startActivity(newIntent)
            }
            else {
                val newIntent = Intent(this, HuespedActivity::class.java)
                if (intent.data != null) {
                    newIntent.data = intent.data
                }
                startActivity(newIntent)
            }
        }
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("ASD", "NEW INTENT")
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