package com.example.bookbnb.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentPerfilBinding
import com.example.bookbnb.databinding.PerfilBindingImpl
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.ui.busqueda.BusquedaLocationFragmentDirections
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.viewmodels.PerfilViewModel
import com.example.bookbnb.viewmodels.PerfilViewModelFactory

class PerfilPublicoFragment : BaseFragment() {

    private val viewModel: PerfilViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, PerfilViewModelFactory(activity.application))
            .get(PerfilViewModel::class.java)
    }

    private lateinit var binding: PerfilBindingImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.perfil,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val args = requireArguments()

        args.getString("userId")?.let {
            viewModel.setUser(it)
        }

        setSnackbarMessageObserver(viewModel, binding.root)
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder), binding.root)

        setNavigateToEditarPerfilObserver()

        return binding.root

    }

    private fun setNavigateToEditarPerfilObserver() {
        viewModel.navigateToEditarPerfil.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    PerfilFragmentDirections.actionPerfilFragmentToEditarPerfilFragment()
                )
                viewModel.onDoneNavigatingToEditarPerfil()
            }
        })
    }
}