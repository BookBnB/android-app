package com.example.bookbnb

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val navController = this.findNavController(R.id.login_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.login_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}