package com.example.bookbnb.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.bookbnb.R

class HomeFragment : Fragment() {

    private val SERVER_URL: String = com.example.bookbnb.BuildConfig.SERVER_URL
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = HomeViewModel()
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val btn = root.findViewById<Button>(R.id.button)
        btn?.setOnClickListener {
            pingServer(btn)
        }
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    /** Called when the user taps the Send button */
    private fun pingServer(view: View) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)
        val url = "%sv1/users".format(SERVER_URL)

        // Request a string response from the provided URL.
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener{ response ->
                // Display the first 500 characters of the response string.
                val message = "Response: %s".format(response.toString())
                homeViewModel.text.setValue(message)
            },
            Response.ErrorListener { error ->
                val message = error.message.toString()
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            })
        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }
}