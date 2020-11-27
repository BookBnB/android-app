package com.example.bookbnb.ui.publicaciones

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentNuevaPublicacionImagesBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModel
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModelFactory

class NuevaPublicacionImagesFragment : BaseFragment() {
    private val PHOTOS_REQUEST_CODE = 10

    private val viewModel: NuevaPublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, NuevaPublicacionViewModelFactory(activity.application))
            .get(NuevaPublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentNuevaPublicacionImagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nueva_publicacion_images,
            container,
            false)

        binding.lifecycleOwner = this

        binding.nuevaPublicacionViewModel = viewModel

        //Only show photos helper if viewmodel doesnt have photos
        if (!viewModel.hasSelectedPhotos()) {
            showSelectPhotosHelp(inflater)
        }

        setPhotosErrorObserver()

        setSelectedPhotosObserver()

        setInitiatePhotoSelectionObserver()

        setSnackbarMessageObserver(viewModel, binding.root)

        setNavigateToPreviewObserver()

        return binding.root
    }

    private fun setNavigateToPreviewObserver() {
        viewModel.navigateToPreviewStep.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    NuevaPublicacionImagesFragmentDirections.actionNuevaPublicacionImagesFragmentToNuevaPublicacionPreviewFragment()
                )
                viewModel.onDoneNavigateToPreviewStep()
            }
        })
    }

    private fun setSelectedPhotosObserver() {
        //TODO: Show photos using bindings with selectedPhotosUri
        viewModel.selectedPhotosUri.observe(viewLifecycleOwner, Observer { imgUris ->
            val adapter = ImagesSliderAdapter(requireContext())
            adapter.renewItems(imgUris.map { CustomImageUri(it) }.toMutableList())
            binding.imageSlider.setSliderAdapter(adapter)
        })
    }

    private fun setInitiatePhotoSelectionObserver() {
        viewModel.initiatePhotoSelection.observe(viewLifecycleOwner, Observer { initiate ->
            if (initiate) {
                startSelectPhotos()
                viewModel.onDoneInitiatingPhotoSelection()
            }
        })
    }

    private fun setPhotosErrorObserver() {
        viewModel.selectPhotosError.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                showSelectPhotosError(it)
                viewModel.onDoneShowingPhotosError()
            }
        })
    }

    private fun showSelectPhotosHelp(inflater: LayoutInflater) {
        val builder = AlertDialog.Builder(context)
        val alertDialog = builder
            .setPositiveButton(
                "Continuar"
            ) { _: DialogInterface?, _: Int -> startSelectPhotos() }
            .create()
        alertDialog.setView(inflater.inflate(R.layout.dialog_gallery, null))
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show()
    }

    private fun startSelectPhotos() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Selecciona las fotografías de tu alojamiento"
            ),
            PHOTOS_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PHOTOS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.handlePhotoFinishSelection(data!!.clipData, data.data)
        }
        else {
            showSelectPhotosError("Ocurrió un error al intentar seleccionar las fotografías.")
        }
    }

    private fun showSelectPhotosError(msg: String) {
        AlertDialog.Builder(context)
            .setTitle("Error al seleccionar las fotografías")
            .setMessage(msg) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Reintentar") { _, _ -> startSelectPhotos() }
            .show()
    }
}