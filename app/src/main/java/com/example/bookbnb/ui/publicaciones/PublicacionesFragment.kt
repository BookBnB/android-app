package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.databinding.FragmentPublicacionesListBinding
import com.example.bookbnb.ui.busqueda.ResultadosBusquedaFragmentDirections
import com.example.bookbnb.viewmodels.PublicacionesViewModel


/**
 * A fragment representing a list of Items.
 */
class PublicacionesFragment : Fragment() {

    private val viewModel: PublicacionesViewModel by lazy {
        ViewModelProvider(this).get(PublicacionesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPublicacionesListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.publicacionesList.adapter = PublicacionRecyclerViewAdapter(PublicacionListener { publicacionId ->
            Toast.makeText(requireContext(), "Publicacion clickeada: $publicacionId", Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(this).navigate(
                ResultadosBusquedaFragmentDirections.actionResultadosBusquedaFragmentToDetallePublicacionFragment(publicacionId)
            )
        }) as PublicacionRecyclerViewAdapter
        binding.publicacionesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.navigateToNewPublicacion.observe(viewLifecycleOwner, Observer {
            if (it){
                NavHostFragment.findNavController(this).navigate(
                    PublicacionesFragmentDirections.actionNavPublicacionesToNuevaPublicacionFragment()
                )
                viewModel.onDoneNavigatingToNuevaPublicacion()
            }
        })

        return binding.root
    }
}