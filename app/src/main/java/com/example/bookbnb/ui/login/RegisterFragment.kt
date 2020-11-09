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
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.example.bookbnb.viewmodels.RegisterViewModel
import com.example.bookbnb.viewmodels.RegisterViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RegisterViewModelFactory(activity.application))
            .get(RegisterViewModel::class.java)
    }

    private lateinit var binding: FragmentRegisterBinding

    private lateinit var spinnerHolder: RelativeLayout

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

        setSpinnerObserver()
        setPossibleUserTypes()
        setNavigateToLoginObserver()
        setSnackbarMessageObserver()

        return binding.root
    }

    private fun setSpinnerObserver() {
        spinnerHolder = requireActivity().findViewById(R.id.spinner_holder)

        viewModel.showLoadingSpinner.observe(viewLifecycleOwner, Observer { show ->
            hideKeyboard()
            spinnerHolder.visibility = if (show) View.VISIBLE else View.GONE
        })
    }

    private fun setSnackbarMessageObserver() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer { msg ->
            msg?.let {
                Snackbar.make(
                    requireActivity().findViewById(R.id.login_activity_layout),
                    it,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onDoneShowingSnackbarMessage()
            }
        })
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}