package com.example.bookbnb.ui.reservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.HuespedReservasRecyclerViewAdapter
import com.example.bookbnb.adapters.ReservaVMListener
import com.example.bookbnb.databinding.FragmentHuespedReservasBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.HuespedReservasViewModel
import com.google.android.material.tabs.TabLayout

class HuespedReservasFragment : BaseFragment() {

    private val viewModel: HuespedReservasViewModel by lazy {
        ViewModelProvider(this).get(HuespedReservasViewModel::class.java)
    }

    private lateinit var binding: FragmentHuespedReservasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_huesped_reservas,
            container,
            false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)

        viewModel.fetchReservasList { viewModel.setSelectedReservasList(requireContext().getString(R.string.reservas_proximas_tab_text)) }
        setReservasRecyclerView(binding)

        setTabLayoutOnSelectedTab()

        return binding.root
    }

    private fun setTabLayoutOnSelectedTab() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setSelectedReservasList(tab?.text.toString())
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun setReservasRecyclerView(binding: FragmentHuespedReservasBinding) {
        binding.reservasList.adapter =
            HuespedReservasRecyclerViewAdapter(ReservaVMListener { reservaId ->
            }) as HuespedReservasRecyclerViewAdapter

        binding.reservasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}