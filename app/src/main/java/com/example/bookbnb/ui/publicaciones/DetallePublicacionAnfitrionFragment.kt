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
import com.example.bookbnb.adapters.PreguntasRecyclerViewAdapter
import com.example.bookbnb.adapters.ResponderPreguntaListener
import com.example.bookbnb.databinding.DialogResponderPreguntaBinding
import com.example.bookbnb.databinding.FragmentDetallePublicacionAnfitrionBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.viewmodels.DetallePublicacionViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionViewModelFactory

class DetallePublicacionAnfitrionFragment : BaseFragment() {

    private val viewModel: DetallePublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, DetallePublicacionViewModelFactory(activity.application))
            .get(DetallePublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentDetallePublicacionAnfitrionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detalle_publicacion_anfitrion,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        setImageSliderObserver()

        setSnackbarMessageObserver(viewModel, binding.root)

        val publicacionId = arguments?.getString("publicacionId")
        setPreguntasListAdapter()

        viewModel.onGetDetail(publicacionId!!)

        binding.detallePublicacionViewModel = viewModel

        setNavigateToReservasListObserver(publicacionId)

        return binding.root
    }

    private fun setPreguntasListAdapter() {
        binding.preguntasList.adapter =
            PreguntasRecyclerViewAdapter(ResponderPreguntaListener { preguntaId ->
                viewModel.selectedPregunta.value = viewModel.preguntas.value?.first { p -> p.id == preguntaId }
                val builder = AlertDialog.Builder(context)
                val binding: DialogResponderPreguntaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_responder_pregunta,
                    null,
                    false
                )
                binding.publicacionViewModel = viewModel
                val reservaDialog = builder
                    .setPositiveButton(
                        "Responder"
                    ) { _: DialogInterface?, _: Int -> viewModel.onEnviarRespuesta() }
                    .setNegativeButton(
                        "Cancelar"
                    ) { _: DialogInterface?, _: Int -> viewModel.cleanRespuesta() }
                    .create()
                reservaDialog.setView(binding.root)
                reservaDialog.show()
            }) as PreguntasRecyclerViewAdapter
        binding.preguntasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setImageSliderObserver() {
        viewModel.publicacion.observe(viewLifecycleOwner, Observer { publicacion ->
            val adapter = ImagesSliderAdapter(requireContext(), loadFromFirebase = true)
            adapter.renewItems(publicacion.imagenes.map { CustomImageUri(Uri.parse(it.url)) }
                .toMutableList())
            binding.detallePublicacion.imageSlider.setSliderAdapter(adapter)
        })
    }

    private fun setNavigateToReservasListObserver(publicacionId: String) {
        viewModel.navigateToReservationList.observe(viewLifecycleOwner, Observer {
            if (it) {
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionAnfitrionFragmentDirections.actionDetallePublicacionAnfitrionFragmentToPagerListasReservasFragment(publicacionId)
                )
                viewModel.onDoneNavigatingToReservationList()
            }
        })
    }
}