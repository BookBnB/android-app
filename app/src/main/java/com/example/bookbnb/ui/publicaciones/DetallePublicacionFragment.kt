package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentDetallePublicacionBinding
import com.example.bookbnb.models.Publicacion
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

        val publicacionString = arguments?.getString("publicacion")
        val publicacionList = publicacionString?.split("|")

        binding.detallePublicacionViewModel = viewModel

        //TODO: buscar una mejor forma de hacerlo
        viewModel.setPublicacion(Publicacion(
            publicacionList?.get(0)!!.toInt(), publicacionList[1], publicacionList[2], publicacionList[3],
        publicacionList[4].toFloat(), publicacionList[5], publicacionList[6], publicacionList[7]))

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        return binding.root
    }

}