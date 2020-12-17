package com.example.bookbnb.ui.reservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.PreguntasRecyclerViewAdapter
import com.example.bookbnb.databinding.FragmentListaReservasBinding
import com.example.bookbnb.ui.publicaciones.PublicacionListener
import com.example.bookbnb.ui.publicaciones.PublicacionRecyclerViewAdapter
import com.example.bookbnb.ui.publicaciones.PublicacionesFragmentDirections
import com.example.bookbnb.viewmodels.ListaReservasViewModel

class ListaReservasFragment(val publicacionId: String, val estadoReserva: String) : Fragment() {

    private val viewModel: ListaReservasViewModel by lazy {
        ViewModelProvider(this).get(ListaReservasViewModel::class.java)
    }

    private lateinit var binding: FragmentListaReservasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lista_reservas,
            container,
            false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setReservasList(binding)
        publicacionId.let { viewModel.onGetReservas(it, estadoReserva) }

        return binding.root
    }

    private fun setReservasList(binding: FragmentListaReservasBinding) {

        binding.reservasList.adapter =
            ReservaRecyclerViewAdapter(ReservaListener { reservaId ->
                NavHostFragment.findNavController(this).navigate(
                    PublicacionesFragmentDirections.actionNavPublicacionesToDetallePublicacionAnfitrionFragment(reservaId)
                )
            }) as ReservaRecyclerViewAdapter

        binding.reservasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

}