package com.example.bookbnb.ui.reservas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentHuespedReservasBinding
import com.example.bookbnb.databinding.FragmentListaReservasBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.HuespedReservasViewModel
import com.example.bookbnb.viewmodels.ListaReservasViewModel

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

        setReservasList(binding)

        setSnackbarMessageObserver(viewModel, binding.root)

        viewModel.setReservasList()

        return binding.root
    }

    private fun setReservasList(binding: FragmentHuespedReservasBinding) {
        binding.reservasList.adapter =
            HuespedReservasRecyclerViewAdapter(ReservaListener { reservaId ->
            }) as HuespedReservasRecyclerViewAdapter

        binding.reservasList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}