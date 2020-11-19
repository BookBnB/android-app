package com.example.bookbnb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.auth0.android.jwt.JWT
import com.example.bookbnb.utils.SessionManager
import com.google.android.material.navigation.NavigationView


class AnfitrionActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var userNotLoggedIn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val jwt = JWT(token)
            setContentView(R.layout.activity_anfitrion)
            val toolbar: Toolbar = findViewById(R.id.toolbar_anfitrion)
            setSupportActionBar(toolbar)

            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_anfitrion)
            val navView: NavigationView = findViewById(R.id.nav_view_anfitrion)

            //Sets the navigation header subtitle
            navView.getHeaderView(0).findViewById<TextView>(R.id.drawer_header_subtitle)?.let {
                it.text = jwt.getClaim("email").asString()
            }

            val navController = findNavController(R.id.nav_host_fragment_anfitrion)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_publicaciones), drawerLayout)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            findViewById<Button>(R.id.logout)?.setOnClickListener{
                SessionManager(this).logout(this)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_anfitrion)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}