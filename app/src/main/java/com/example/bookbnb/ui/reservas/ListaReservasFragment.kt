package com.example.bookbnb.ui.reservas

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.databinding.DialogConfirmacionReservaBinding
import com.example.bookbnb.databinding.DialogReservaAceptadaBinding
import com.example.bookbnb.databinding.FragmentListaReservasBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.ListaReservasViewModel

class ListaReservasFragment(val publicacionId: String, val estadoReserva: String) : BaseFragment() {

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

        setSnackbarMessageObserver(viewModel, binding.root)

        setConfirmacionReservaObserver(inflater)
        setReservaAceptadaObserver(inflater)

        return binding.root
    }

    private fun setReservasList(binding: FragmentListaReservasBinding) {
        binding.reservasList.adapter =
            ReservaRecyclerViewAdapter(ReservaListener { reservaId ->
                viewModel.onAceptacionReserva(reservaId)
            }) as ReservaRecyclerViewAdapter

        binding.reservasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setConfirmacionReservaObserver(inflater: LayoutInflater) {
        viewModel.showConfirmacionReserva.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = AlertDialog.Builder(context)
                val binding: DialogConfirmacionReservaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_confirmacion_reserva,
                    null,
                    false
                )

                binding.textoConfirmacionReserva.text.toString().replace("{}",
                    viewModel.ultimaReservaAceptada.value!!
                )

                binding.reservasViewModel = viewModel
                val reservaAceptadaDialog = builder
                    .setPositiveButton(
                        "Si"
                    ) { _: DialogInterface?, _: Int -> viewModel.confirmarReserva() }
                    .setNegativeButton(
                        "No"
                    ) { _: DialogInterface?, _: Int -> viewModel.cerrarDialog() }
                    .create()
                reservaAceptadaDialog.setView(binding.root)
                reservaAceptadaDialog.show()
                viewModel.onDoneShowingConfirmacionReserva()
            }
        })
    }

    private fun setReservaAceptadaObserver(inflater: LayoutInflater) {
        viewModel.showReservaAceptada.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = AlertDialog.Builder(context)
                val binding: DialogReservaAceptadaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_reserva_aceptada,
                    null,
                    false
                )

                binding.textoReservaAceptada.text.toString().replace("{}",
                    viewModel.ultimaReservaAceptada.value!!
                )

                binding.reservasViewModel = viewModel
                val reservaAceptadaDialog = builder
                    .setPositiveButton(
                        "Aceptar"
                    ) { _: DialogInterface?, _: Int -> viewModel.cerrarDialog() }
                    .create()
                reservaAceptadaDialog.setView(binding.root)
                reservaAceptadaDialog.show()
                viewModel.onDoneShowingReservaAceptada()

            }
        })
    }

}