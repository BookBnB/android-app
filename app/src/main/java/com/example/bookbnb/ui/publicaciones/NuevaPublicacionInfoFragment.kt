package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentNuevaPublicacionInfoBinding
import com.example.bookbnb.ui.login.RegisterFragmentDirections
import com.example.bookbnb.viewmodels.*

class NuevaPublicacionInfoFragment : Fragment() {

    private val viewModel: NuevaPublicacionViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, NuevaPublicacionViewModelFactory(activity.application))
            .get(NuevaPublicacionViewModel::class.java)
    }

    private lateinit var binding: FragmentNuevaPublicacionInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nueva_publicacion_info,
            container,
            false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.nuevaPublicacionViewModel = viewModel

        setNavigateToNextStepObserver()

        return binding.root
    }

    private fun setNavigateToNextStepObserver() {
        viewModel.navigateToMapStep.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    NuevaPublicacionInfoFragmentDirections.actionNuevaPublicacionFragmentToNuevaPublicacionLocationFragment()
                )
                viewModel.onDoneNavigatingToMapStep()
            }
        })
    }
}