package com.example.bookbnb.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.databinding.FragmentRegisterBinding
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.example.bookbnb.viewmodels.RegisterViewModel
import com.example.bookbnb.viewmodels.RegisterViewModelFactory

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RegisterViewModelFactory(activity.application))
            .get(RegisterViewModel::class.java)
    }

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register,
            container,
            false)

        binding.lifecycleOwner = this

        binding.registerViewModel = viewModel

        setPossibleUserTypes()

        return binding.root
    }

    private fun setPossibleUserTypes() {
        val items = listOf(getString(R.string.user_type_huesped), getString(R.string.user_type_anfitrion))
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setText(getString(R.string.user_type_huesped), false) //Defaults to huesped
    }

}