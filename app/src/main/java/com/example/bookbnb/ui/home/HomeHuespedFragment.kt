package com.example.bookbnb.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.databinding.FragmentHomeHuespedBinding
import com.example.bookbnb.viewmodels.HomeHuespedViewModel

class HomeHuespedFragment : Fragment() {

    private val viewModel: HomeHuespedViewModel by lazy {
        ViewModelProvider(this).get(HomeHuespedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeHuespedBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.homeHuespedViewModel = viewModel
        setNavigateToBusquedaObserver()

        return binding.root
    }

    private fun setNavigateToBusquedaObserver() =
        viewModel.navigateToBusqueda.observe(viewLifecycleOwner, Observer {
            if (it) {
                NavHostFragment.findNavController(this).navigate(
                    HomeHuespedFragmentDirections.actionNavHomeHuespedToBusquedaFragment()
                )
                viewModel.onDoneNavigatingToBusqueda()
            }
        })
}