package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentNuevaPublicacionPreviewBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.CustomImage
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModel
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModelFactory

class NuevaPublicacionPreviewFragment : BaseFragment() {

    private val viewModel: NuevaPublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, NuevaPublicacionViewModelFactory(activity.application))
            .get(NuevaPublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentNuevaPublicacionPreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nueva_publicacion_preview,
            container,
            false)

        binding.lifecycleOwner = this

        binding.nuevaPublicacionViewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)

        val adapter = ImagesSliderAdapter(requireContext())
        adapter.renewItems(viewModel.selectedPhotosUri.value!!.map { CustomImage(it) }.toMutableList())
        binding.imageSlider.setSliderAdapter(adapter)

        setSpinnerObserver(viewModel, binding.spinnerHolder)

        setNavigateToPublicacionesObserver()

        return binding.root
    }

    private fun setNavigateToPublicacionesObserver() {
        viewModel.navigateToPublicaciones.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    NuevaPublicacionPreviewFragmentDirections.actionNuevaPublicacionPreviewFragmentToNavPublicaciones()
                )
                viewModel.onDoneNavigatingToPublicaciones()
            }
        })
    }
}

