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
import com.example.bookbnb.databinding.FragmentEditarPerfilBinding
import com.example.bookbnb.databinding.FragmentPerfilBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.viewmodels.PerfilViewModel
import com.example.bookbnb.viewmodels.PerfilViewModelFactory

class EditarPerfilFragment : BaseFragment() {

    private val viewModel: PerfilViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, PerfilViewModelFactory(activity.application))
            .get(PerfilViewModel::class.java)
    }

    private lateinit var binding: FragmentEditarPerfilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_editar_perfil,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //viewModel.setUser(SessionManager(requireContext()).getUserId()!!)

        setSnackbarMessageObserver(viewModel, binding.root)
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder), binding.root)

        viewModel.navigateToPerfil.observe(viewLifecycleOwner, Observer {navigate ->
            if (navigate){
                hideKeyboard()
                NavHostFragment.findNavController(this).navigate(
                    EditarPerfilFragmentDirections.actionEditarPerfilFragmentToPerfilFragment()
                )
                viewModel.onDoneNavigatingToPerfil()
            }

        })

        return binding.root

    }
}