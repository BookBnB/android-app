package com.example.bookbnb.ui.publicaciones

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.DialogReservaBinding
import com.example.bookbnb.databinding.FragmentDetallePublicacionBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.viewmodels.DetallePublicacionViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker


class DetallePublicacionFragment : BaseFragment() {

    private val viewModel: DetallePublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, DetallePublicacionViewModelFactory(activity.application))
            .get(DetallePublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentDetallePublicacionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detalle_publicacion,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.detallePublicacionViewModel = viewModel

        setImageSliderObserver()

        setDisplayDisponibilidadObserver()

        setDisplayReservaPrecioDialogObserver(inflater)

        viewModel.navigateToReservationComplete.observe(viewLifecycleOwner, Observer {navigate ->
            if (navigate){
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionFragmentDirections.actionDetallePublicacionFragmentToReservaRealizadaFragment()
                )
                viewModel.onDoneNavigatingToReservationComplete()
            }
        })

        val publicacionId = arguments?.getString("publicacionId")
        viewModel.onGetDetail(publicacionId!!)

        return binding.root
    }

    private fun setImageSliderObserver() {
        viewModel.publicacion.observe(viewLifecycleOwner, Observer { publicacion ->
            val adapter = ImagesSliderAdapter(requireContext(), loadFromFirebase = true)
            adapter.renewItems(publicacion.imagenes.map { CustomImageUri(Uri.parse(it.url)) }
                .toMutableList())
            binding.imageSlider.setSliderAdapter(adapter)
        })
    }

    private fun setDisplayDisponibilidadObserver() {
        viewModel.displayDisponibilidadDialog.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = MaterialDatePicker.Builder.dateRangePicker()
                builder.setTitleText("Seleccione la fecha de llegada y de salida")
                val picker = builder.build()
                picker.show(childFragmentManager, picker.toString())
                picker.addOnPositiveButtonClickListener {
                    if (it.first != null && it.second != null) {
                        viewModel.setDisponibilidadElegida(it.first!!, it.second!!)
                    }
                }
                viewModel.onDoneReservaButtonClick()
            }
        })
    }

    private fun setDisplayReservaPrecioDialogObserver(inflater: LayoutInflater){
        viewModel.showReservaPrecioDialog.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = AlertDialog.Builder(context)
                val binding: DialogReservaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_reserva,
                    null,
                    false
                )
                binding.publicacionViewModel = viewModel
                val reservaDialog = builder
                    .setPositiveButton(
                        "Enviar Reserva"
                    ) { dialog: DialogInterface?, which: Int -> viewModel.endReservation() }
                    .setNegativeButton(
                        "Cancelar"
                    ) { dialog: DialogInterface?, which: Int -> viewModel.cancelReservation() }
                    .create()
                reservaDialog.setView(binding.root)
                reservaDialog.show()
                viewModel.onDoneShowingReservaPrecio()
            }
        })
    }

}