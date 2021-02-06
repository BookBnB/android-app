package com.example.bookbnb.ui.reservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.bookbnb.R
import com.example.bookbnb.adapters.ReservasPagerAdapter
import com.example.bookbnb.databinding.FragmentListaReservasBinding
import com.example.bookbnb.databinding.FragmentPagerListasReservasBinding
import com.google.android.material.tabs.TabLayout

class PagerListasReservasFragment : Fragment() {

    private lateinit var binding: FragmentPagerListasReservasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pager_listas_reservas,
            container,
            false)

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val publicacionId = arguments?.getString("publicacionId")!!

        val reservasPagerAdapter= ReservasPagerAdapter(
            requireContext(),
            childFragmentManager,
            publicacionId
        )

        binding.viewPager.adapter = reservasPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }
}