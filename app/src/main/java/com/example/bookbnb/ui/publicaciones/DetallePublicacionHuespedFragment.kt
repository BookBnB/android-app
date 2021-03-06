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
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.CalificacionesRecyclerViewAdapter
import com.example.bookbnb.adapters.PreguntasRecyclerViewAdapter
import com.example.bookbnb.databinding.DialogReservaBinding
import com.example.bookbnb.databinding.FragmentDetallePublicacionHuespedBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.viewmodels.DetallePublicacionHuespedViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionHuespedViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class DetallePublicacionHuespedFragment : BaseFragment() {

    private val viewModel: DetallePublicacionHuespedViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, DetallePublicacionHuespedViewModelFactory(activity.application))
            .get(DetallePublicacionHuespedViewModel::class.java)
    }

    private lateinit var binding: FragmentDetallePublicacionHuespedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detalle_publicacion_huesped,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this


        setImageSliderObserver()

        setDisplayDisponibilidadObserver()

        setDisplayReservaDialogObserver(inflater)

        setReservationCompleteObserver()

        setSnackbarMessageObserver(viewModel, binding.root)

        setSnackbarMessageObserver(viewModel, binding.root)
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder), binding.root)

        val args = requireArguments()
        val publicacionId = args.getString("publicacionId")
        val startDate: Date? = args.getSerializable("startDate") as Date?
        val endDate: Date? = args.getSerializable("endDate") as Date?
        startDate?.time?.let { start -> endDate?.time?.let { end ->
            viewModel.setDisponibilidadElegida(start, end)
        } }
        setPreguntasListAdapter()
        setCalificacionesListAdapter()

        setNavigateToChatObserver()
        setNavigateToPerfilObserver()

        viewModel.onGetDetail(publicacionId!!)

        binding.detallePublicacionViewModel = viewModel

        return binding.root
    }

    private fun setCalificacionesListAdapter() {
        binding.valoracionesPublicacion.calificacionesList.adapter =
            CalificacionesRecyclerViewAdapter() as CalificacionesRecyclerViewAdapter
        binding.valoracionesPublicacion.calificacionesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setNavigateToPerfilObserver() {
        viewModel.navigateToPerfil.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionHuespedFragmentDirections
                        .actionDetallePublicacionFragmentToPerfilPublicoFragment(
                            viewModel.publicacion.value?.anfitrion?.id!!
                        )
                )
                viewModel.onDoneNavigatingToPerfil()
            }
        })
    }

    private fun setNavigateToChatObserver() {
        viewModel.navigateToChat.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                val sessionManager = SessionManager(requireContext())
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionHuespedFragmentDirections
                        .actionDetallePublicacionFragmentToChatFragment(
                            sessionManager.getUserId()!!,
                            viewModel.publicacion.value?.anfitrion?.id!!
                        )
                )
                viewModel.onEndNavigatingToChat()
            }
        })
    }

    private fun setPreguntasListAdapter() {
        binding.preguntasList.adapter =
            PreguntasRecyclerViewAdapter() as PreguntasRecyclerViewAdapter
        binding.preguntasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setReservationCompleteObserver() {
        viewModel.navigateToReservationComplete.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionHuespedFragmentDirections.actionDetallePublicacionFragmentToReservaRealizadaFragment(viewModel.reservaRealizadaId.value!!)
                )
                viewModel.onDoneNavigatingToReservationComplete()
            }
        })
    }

    private fun setImageSliderObserver() {
        viewModel.publicacion.observe(viewLifecycleOwner, Observer { publicacion ->
            val adapter = ImagesSliderAdapter(requireContext(), loadFromFirebase = true)
            adapter.renewItems(publicacion.imagenes.map { CustomImageUri(Uri.parse(it.url)) }
                .toMutableList())
            binding.detallePublicacion.imageSlider.setSliderAdapter(adapter)
        })
    }

    private fun setDisplayDisponibilidadObserver() {
        viewModel.displayDisponibilidadDialog.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                if (viewModel.hasDisponibilidadElegida()){
                    viewModel.showReservaDialog()
                }
                else {
                    val builder = MaterialDatePicker.Builder.dateRangePicker()
                    val calendarConstraintsBuilder = CalendarConstraints.Builder()
                    calendarConstraintsBuilder.setValidator(DateValidatorPointForward.now())
                    builder.setCalendarConstraints(calendarConstraintsBuilder.build())
                    builder.setTitleText("Seleccione la fecha de llegada y de salida")
                    val picker = builder.build()
                    picker.show(childFragmentManager, picker.toString())
                    picker.addOnPositiveButtonClickListener {
                        if (it.first != null && it.second != null) {
                            viewModel.setDisponibilidadElegida(it.first!!, it.second!!)
                            viewModel.showReservaDialog()
                        }
                    }
                }
                viewModel.onDoneReservaButtonClick()
            }
        })
    }

    private fun setDisplayReservaDialogObserver(inflater: LayoutInflater){
        viewModel.showReservaDialog.observe(viewLifecycleOwner, Observer { display ->
            if (display) {
                val builder = AlertDialog.Builder(context)
                val binding: DialogReservaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_reserva,
                    null,
                    false
                )
                viewModel.setReservaTotalPrice()
                binding.publicacionViewModel = viewModel
                val reservaDialog = builder
                    .setPositiveButton(
                        "Reservar"
                    ) { _: DialogInterface?, _: Int -> viewModel.endReservation() }
                    .setNegativeButton(
                        "Cancelar"
                    ) { _: DialogInterface?, _: Int -> viewModel.cancelReservation() }
                    .create()
                reservaDialog.setView(binding.root)
                reservaDialog.show()
                viewModel.onDoneShowingReservaConfirm()
            }
        })
    }

}