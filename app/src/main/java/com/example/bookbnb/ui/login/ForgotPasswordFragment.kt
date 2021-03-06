package com.example.bookbnb.ui.login

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentForgotPasswordBinding
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.models.User
import com.example.bookbnb.network.BookBnBApi
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.network.ResultWrapper
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.ui.publicaciones.NuevaPublicacionInfoFragmentDirections
import com.example.bookbnb.viewmodels.ForgotPasswordViewModel
import com.example.bookbnb.viewmodels.ForgotPasswordViewModelFactory
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import kotlinx.coroutines.launch

class ForgotPasswordFragment : BaseFragment() {

    private val viewModel: ForgotPasswordViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, ForgotPasswordViewModelFactory(activity.application))
            .get(ForgotPasswordViewModel::class.java)
    }

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password,
            container,
            false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder))
        setSnackbarMessageObserver(viewModel, requireActivity().findViewById(R.id.login_activity_layout))

        viewModel.navigateToSucess.observe(viewLifecycleOwner, Observer{navigate ->
            if (navigate){
                NavHostFragment.findNavController(this).navigate(
                    ForgotPasswordFragmentDirections.actionForgotPasswordToEmailSentFragment()
                )
                viewModel.onDoneNavigatingToEmailSent()
            }
        })

        return binding.root
    }
}