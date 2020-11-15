package com.example.bookbnb.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginFragment : BaseFragment(){

    private val viewModel: LoginViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, LoginViewModelFactory(activity.application))
            .get(LoginViewModel::class.java)
    }

    private lateinit var binding: FragmentLoginBinding

    private lateinit var spinnerHolder: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false)

        binding.lifecycleOwner = this

        binding.loginViewModel = viewModel

        setNavigateToMainActivityObserver()
        setNavigateToRegisterObserver()
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder))
        setSnackbarMessageObserver(viewModel, requireActivity().findViewById(R.id.login_activity_layout))

        return binding.root
    }

    private fun setNavigateToRegisterObserver() {
        viewModel.navigateToRegister.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                NavHostFragment.findNavController(this).navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
                viewModel.onDoneNavigateToRegister()
            }
        })
    }

    private fun setNavigateToMainActivityObserver() {
        viewModel.navigateToMainActivity.observe(viewLifecycleOwner, Observer {
            if (it) {
                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            }
        })
    }
}