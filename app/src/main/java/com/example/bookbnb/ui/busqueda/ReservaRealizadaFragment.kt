package com.example.bookbnb.ui.busqueda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.R
import com.example.bookbnb.viewmodels.DetallePublicacionHuespedViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionHuespedViewModelFactory
import com.example.bookbnb.viewmodels.DetallePublicacionViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionViewModelFactory

class ReservaRealizadaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_reserva_realizada, container, false)

        view.findViewById<TextView>(R.id.reservaIdTextView)?.text = requireArguments().getString("reservaId")
        // Inflate the layout for this fragment
        return view
    }

}