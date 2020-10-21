package com.example.bookbnb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

const val EXTRA_MESSAGE = "com.example.bookbnb.MESSAGE"

class MainActivity : AppCompatActivity() {

    private val SERVER_URL: String = BuildConfig.SERVER_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Called when the user taps the Send button */
    fun pingServer(view: View) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "%sv1/users".format(SERVER_URL)
        Toast.makeText(this, url, Toast.LENGTH_LONG).show()

        // Request a string response from the provided URL.
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener{ response ->
                // Display the first 500 characters of the response string.
                val message = "Response: %s".format(response.toString())
                val intent = Intent(this, DisplayMessageActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, message)
                }
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                val message = error.message.toString()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            })
        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }
}