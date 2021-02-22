package com.example.bookbnb.ui.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentResultadosBusquedaBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.ui.publicaciones.PublicacionListener
import com.example.bookbnb.ui.publicaciones.PublicacionRecyclerViewAdapter
import com.example.bookbnb.viewmodels.BusquedaViewModel
import com.example.bookbnb.viewmodels.BusquedaViewModelFactory

class ResultadosBusquedaFragment : BaseFragment(){

    private val viewModel: BusquedaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, BusquedaViewModelFactory(activity.application))
            .get(BusquedaViewModel::class.java)
    }
    private lateinit var binding: FragmentResultadosBusquedaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_resultados_busqueda,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.resultadosBusqueda.adapter = PublicacionRecyclerViewAdapter(PublicacionListener { publicacionId ->
            NavHostFragment.findNavController(this).navigate(
                    ResultadosBusquedaFragmentDirections.actionResultadosBusquedaFragmentToDetallePublicacionFragment(publicacionId,
                        viewModel.startDate.value, viewModel.endDate.value)
                )
        }, true) as PublicacionRecyclerViewAdapter

        binding.resultadosBusqueda.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.getResults()

        binding.resultadosBusquedaViewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)

        return binding.root
    }

}