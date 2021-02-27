package com.example.bookbnb.ui.reservas

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.HuespedReservasRecyclerViewAdapter
import com.example.bookbnb.adapters.ReservaVMListener
import com.example.bookbnb.databinding.*
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.ReservaDialogConfirmProvider
import com.example.bookbnb.utils.notifyObserver
import com.example.bookbnb.viewmodels.HuespedReservasViewModel
import com.example.bookbnb.viewmodels.ReservaVM
import com.google.android.material.tabs.TabLayout

class HuespedReservasFragment : BaseFragment() {

    private val viewModel: HuespedReservasViewModel by lazy {
        ViewModelProvider(this).get(HuespedReservasViewModel::class.java)
    }

    private lateinit var binding: FragmentHuespedReservasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_huesped_reservas,
            container,
            false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder), binding.root)

        viewModel.fetchReservasList { viewModel.setSelectedReservasList(requireContext().getString(R.string.reservas_proximas_tab_text)) }
        setReservasRecyclerView(binding)

        setTabLayoutOnSelectedTab()

        return binding.root
    }

    private fun setTabLayoutOnSelectedTab() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setSelectedReservasList(tab?.text.toString())
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun setReservasRecyclerView(binding: FragmentHuespedReservasBinding) {
        binding.reservasList.adapter =
            HuespedReservasRecyclerViewAdapter(ReservaVMListener { reservaVM ->
                showDialogCalificacion(reservaVM)
            }, ReservaVMListener { reservaVM ->
                showDialogConfirmCancelacion(reservaVM.reserva)
            }) as HuespedReservasRecyclerViewAdapter

        binding.reservasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun showDialogCalificacion(
        reservaVM: ReservaVM
    ) {
        val builder = AlertDialog.Builder(context)
        val bindingDialog: DialogCalificacionAlojamientoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_calificacion_alojamiento,
            null,
            false
        )
        bindingDialog.reservaViewModel = reservaVM
        val calificacionDialog = builder
            .setPositiveButton(
                "Calificar"
            ) { _: DialogInterface?, _: Int ->
                run {
                    bindingDialog.reservaViewModel.let {
                        if (it?.rating?.value == null || it.resenia.value.isNullOrEmpty()){
                            viewModel.showSnackbarErrorMessage("¡Oops! No se ingresó calificación/reseña para el alojamiento.")
                        }
                        else {
                            viewModel.enviarCalificacion(it.reserva, it.rating.value!!, it.resenia.value!!)
                            it.isCalificada.value = true
                            viewModel.reservas.notifyObserver()
                        }
                    }
                }
            }
            .setNegativeButton(
                "Cancelar"
            ) { _: DialogInterface?, _: Int -> bindingDialog.reservaViewModel = null }
            .create()
        calificacionDialog.setView(bindingDialog.root)
        calificacionDialog.show()
    }

    private fun showDialogConfirmCancelacion(selectedReserva : Reserva) {
        ReservaDialogConfirmProvider.showDialogConfirm(
            requireContext(),
            String.format(resources.getString(R.string.cancelar_reserva_format), selectedReserva.id),
            selectedReserva,
            viewModel::cancelarReserva
        )
/*
        val builder = AlertDialog.Builder(context)
        val bindingDialog: DialogConfirmacionCancelarReservaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_confirmacion_cancelar_reserva,
            null,
            false
        )
        bindingDialog.viewModel = selectedReserva
        val reservaAceptadaDialog = builder
            .setPositiveButton(
                "Confirmar"
            ) { _: DialogInterface?, _: Int -> viewModel.cancelarReserva(selectedReserva) }
            .setNegativeButton(
                "Cancelar"
            ) { _: DialogInterface?, _: Int -> bindingDialog.viewModel = null }
            .create()
        reservaAceptadaDialog.setView(bindingDialog.root)
        reservaAceptadaDialog.show()
        */
    }
}