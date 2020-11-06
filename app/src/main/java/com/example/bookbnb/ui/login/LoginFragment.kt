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
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment(){
    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
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
        setSpinnerObserver()

        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer { msg ->
            msg?.let {
                Snackbar.make(
                    requireActivity().findViewById(R.id.login_layout),
                    msg,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onDoneShowingSnackbarMessage()
            }
        })

        return binding.root
    }

    private fun setSpinnerObserver() {
        spinnerHolder = requireActivity().findViewById(R.id.spinner_holder)

        viewModel.showLoadingSpinner.observe(viewLifecycleOwner, Observer { show ->
            hideKeyboard()
            spinnerHolder.visibility = if (show) View.VISIBLE else View.GONE
        })
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
            }
        })
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}