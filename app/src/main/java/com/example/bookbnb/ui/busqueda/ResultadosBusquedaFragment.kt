package com.example.bookbnb.ui.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentResultadosBusquedaBinding
import com.example.bookbnb.ui.publicaciones.PublicacionRecyclerViewAdapter
import com.example.bookbnb.viewmodels.ResultadosBusquedaViewModel
import com.example.bookbnb.viewmodels.ResultadosBusquedaViewModelFactory

class ResultadosBusquedaFragment : Fragment(){

    private val viewModel: ResultadosBusquedaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, ResultadosBusquedaViewModelFactory(activity.application))
            .get(ResultadosBusquedaViewModel::class.java)
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

        binding.resultadosBusquedaViewModel = viewModel

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.resultadosBusqueda.adapter = PublicacionRecyclerViewAdapter() as PublicacionRecyclerViewAdapter
        binding.resultadosBusqueda.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.navigateToDetallePublicacion.observe(viewLifecycleOwner, Observer {
            if (it){
                NavHostFragment.findNavController(this).navigate(
                    DetallePublicacionFragmentDirections.actionNavResultadosBusquedaToDetallePublicacionFragment()
                )
                viewModel.onDoneNavigateToDetallePublicacion()
            }
        })

        return binding.root
    }

}