package com.example.bookbnb.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.databinding.FragmentRegisterBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.example.bookbnb.viewmodels.RegisterViewModel
import com.example.bookbnb.viewmodels.RegisterViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RegisterViewModelFactory(activity.application))
            .get(RegisterViewModel::class.java)
    }

    private lateinit var binding: FragmentRegisterBinding

    private lateinit var spinnerHolder: View

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

        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder))
        setPossibleUserTypes()
        setNavigateToLoginObserver()
        setSnackbarMessageObserver(viewModel,
            requireActivity().findViewById(R.id.login_activity_layout))

        return binding.root
    }

    private fun setNavigateToLoginObserver() {
        viewModel.navigateToLogin.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
                viewModel.onDoneNavigateToLogin()
            }
        })
    }

    private fun setPossibleUserTypes() {
        val items = listOf(getString(R.string.user_type_huesped), getString(R.string.user_type_anfitrion))
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setText(getString(R.string.user_type_huesped), false) //Defaults to huesped
    }

}