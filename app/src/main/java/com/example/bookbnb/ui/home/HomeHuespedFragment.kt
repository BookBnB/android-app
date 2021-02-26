package com.example.bookbnb.ui.home

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentHomeHuespedBinding
import com.example.bookbnb.viewmodels.HomeHuespedViewModel
import com.google.android.material.appbar.AppBarLayout


class HomeHuespedFragment : Fragment() {

    private val viewModel: HomeHuespedViewModel by lazy {
        ViewModelProvider(this).get(HomeHuespedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val appbar = requireActivity().findViewById<AppBarLayout>(R.id.appbar_huesped)
        appbar.isLiftOnScroll = true
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_huesped)
        toolbar.background = ContextCompat.getDrawable(requireContext(), R.color.secondaryLightColor)

        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.secondaryLightColor)
        }

        val binding = FragmentHomeHuespedBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.homeHuespedViewModel = viewModel
        setNavigateToBusquedaObserver()

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val appbar = requireActivity().findViewById<AppBarLayout>(R.id.appbar_huesped)
        appbar.isLiftOnScroll = false
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_huesped)
        toolbar.background = ContextCompat.getDrawable(requireContext(), R.color.primaryColor)
        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)
        }
    }

    private fun setNavigateToBusquedaObserver() =
        viewModel.navigateToBusqueda.observe(viewLifecycleOwner, Observer {
            if (it) {
                NavHostFragment.findNavController(this).navigate(
                    HomeHuespedFragmentDirections.actionNavHomeHuespedToBusquedaLocationFragment()
                )
                viewModel.onDoneNavigatingToBusqueda()
            }
        })
}