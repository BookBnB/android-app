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
    private val viewModel: DetallePublicacionHuespedViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, DetallePublicacionHuespedViewModelFactory(activity.application))
            .get(DetallePublicacionHuespedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_reserva_realizada, container, false)

        view.findViewById<TextView>(R.id.reservaIdTextView)?.text = viewModel.reservaRealizadaId.value
        // Inflate the layout for this fragment
        return view
    }

}