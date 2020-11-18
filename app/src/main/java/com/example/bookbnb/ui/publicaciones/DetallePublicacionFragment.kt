package com.example.bookbnb.ui.publicaciones

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentDetallePublicacionBinding
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.viewmodels.DetallePublicacionViewModel
import com.example.bookbnb.viewmodels.DetallePublicacionViewModelFactory

class DetallePublicacionFragment : Fragment() {

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

        viewModel.publicacion.observe(viewLifecycleOwner, Observer {publicacion ->
            val adapter = ImagesSliderAdapter(requireContext(), loadFromFirebase = true)
            adapter.renewItems(publicacion.imagenes.map { CustomImageUri(Uri.parse(it.url)) }.toMutableList())
            binding.imageSlider.setSliderAdapter(adapter)
        })

        val publicacionId = arguments?.getString("publicacionId")
        viewModel.onGetDetail(publicacionId!!)

        return binding.root
    }

}