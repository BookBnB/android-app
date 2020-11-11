package com.example.bookbnb.ui.publicaciones

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentNuevaPublicacionLocationBinding
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModel
import com.example.bookbnb.viewmodels.NuevaPublicacionViewModelFactory
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker


class NuevaPublicacionLocationFragment : Fragment() {
    companion object {
        private const val ZOOM_START = 9.5
        private const val ZOOM_SELECTION = 17.5
        private val STARTING_POINT: GeoPoint = GeoPoint(-34.6228237, -58.4563738); //Starts on CABA
        private const val REQUEST_PERMISSION_CODE = 1
        private const val MIN_CHARS_TO_SUGGEST_LOCATION = 3
    }
    private val viewModel: NuevaPublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, NuevaPublicacionViewModelFactory(activity.application))
            .get(NuevaPublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentNuevaPublicacionLocationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nueva_publicacion_location,
            container,
            false
        )

        binding.nuevaPublicacionViewModel = viewModel
        binding.lifecycleOwner = this

        setupMap()

        requestPermissionsIfNecessary(Array<String>(1) {
            // if you need to show the current location, uncomment the line below
            // Manifest.permission.ACCESS_FINE_LOCATION,
            // WRITE_EXTERNAL_STORAGE is required in order to show the map
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        setLocationTextObserver()
        setSuggestionOnClick()
        setNavigateToNextStepObserver()

        return binding.root
    }

    private fun setNavigateToNextStepObserver() {
        viewModel.navigateToImagesStep.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    NuevaPublicacionLocationFragmentDirections.actionNuevaPublicacionLocationFragmentToNuevaPublicacionImagesFragment()
                )
                viewModel.onDoneNavigatingToImagesStep()
            }
        })
    }

    private fun setSuggestionOnClick() {
        binding.searchText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                val item = viewModel.autocompleteLocationAdapter.value?.getItem(pos)
                viewModel.setSelectedLocation(item)
                setMarkerOnMap(item)
                binding.searchText.setSelection(0) //Focus on the start of the TextView
                hideKeyboard()
            }
    }

    private fun setMarkerOnMap(item: CustomLocation?) {
        if (item != null){
            val mapPoint = GeoPoint(item.coordenadas.latitud, item.coordenadas.longitud)
            val marker = Marker(binding.map)
            marker.title = viewModel.titulo.value
            marker.subDescription = viewModel.desc.value
            marker.position = mapPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.map.apply {
                overlays.clear()
                overlays.add(marker)
                controller.setCenter(mapPoint)
                controller.setZoom(ZOOM_SELECTION);
            }
        }
    }

    private fun setLocationTextObserver() {
        viewModel.locationText.observe(viewLifecycleOwner, Observer { location ->
            if (location.length >= MIN_CHARS_TO_SUGGEST_LOCATION) {
                viewModel.searchLocation(location)
            }
        })
    }

    private fun setupMap() {
        //load/initialize the osmdroid configuration, this can be done
        val ctx: Context = requireContext()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        binding.map.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            zoomController?.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
            setMultiTouchControls(true);
            controller?.setZoom(ZOOM_START);
            controller.setCenter(STARTING_POINT)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        val permissionsToRequest: ArrayList<String?> = ArrayList()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        setMarkerOnMap(viewModel.selectedLocation.value) //Redraw marker on rotate and on navigating forward and back
        binding.map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}