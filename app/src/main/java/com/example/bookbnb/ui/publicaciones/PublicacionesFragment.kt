package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentNuevaPublicacionInfoBinding
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

    private lateinit var binding: FragmentPublicacionesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_publicaciones_list,
            container,
            false)
        //val binding = FragmentPublicacionesListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setPublicacionesList(binding)

        setNavigateToNewPublicacionObserver()

        return binding.root
    }

    private fun setPublicacionesList(binding: FragmentPublicacionesListBinding) {
        binding.publicacionesList.adapter =
            PublicacionRecyclerViewAdapter(PublicacionListener { publicacionId ->

            }, true) as PublicacionRecyclerViewAdapter
        binding.publicacionesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setNavigateToNewPublicacionObserver() {
        viewModel.navigateToNewPublicacion.observe(viewLifecycleOwner, Observer {
            if (it) {
                NavHostFragment.findNavController(this).navigate(
                    PublicacionesFragmentDirections.actionNavPublicacionesToNuevaPublicacionFragment()
                )
                viewModel.onDoneNavigatingToNuevaPublicacion()
            }
        })
    }
}