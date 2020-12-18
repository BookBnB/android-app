package com.example.bookbnb.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.auth0.android.jwt.JWT
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.databinding.DialogRegisterBinding
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
import com.example.bookbnb.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginFragment : BaseFragment(){
    private var TAG : String = "LoginFragment"
    private val REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val viewModel: LoginViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, LoginViewModelFactory(activity.application))
            .get(LoginViewModel::class.java)
    }

    private lateinit var binding: FragmentLoginBinding

    private lateinit var spinnerHolder: RelativeLayout

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            Log.w(TAG, account.idToken.toString())
            mGoogleSignInClient.signOut()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.web_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

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
        setSignInWithGoogleObserver()
        setSignUpWithGoogleObserver()
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder))
        setSnackbarMessageObserver(viewModel, requireActivity().findViewById(R.id.login_activity_layout))

        return binding.root
    }

    private fun setSignInWithGoogleObserver(){
        viewModel.showGoogleSignIn.observe(viewLifecycleOwner, Observer { show ->
            if (show){
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, REQ_ONE_TAP)
                viewModel.onDoneShowingGoogleSignInClick()
            }
        })
    }

    private fun setSignUpWithGoogleObserver(){
        viewModel.showGoogleSignUp.observe(viewLifecycleOwner, Observer { show ->
            if (show){
                showGoogleSignUpDialog(viewModel.googleToken.value!!)
                viewModel.onDoneShowingGoogleSignUpWithGoogle()
            }
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
                requireActivity().finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            Log.w(TAG, account.idToken.toString())
            viewModel.onGoogleLogin(account.idToken!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            viewModel.showSnackbarErrorMessage("Algo salió mal al querer contactarse con google. Detalles: ${e.message}")
        }
    }

    private fun showGoogleSignUpDialog(token: String){
        val binding: DialogRegisterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_register,
            null,
            false
        )
        var registerViewModel = RegisterViewModel(requireActivity().application)
        registerViewModel.setDataFromGoogleToken(token)
        setLogUserWithGoogleAfterRegistrationObserver(registerViewModel)
        binding.registerViewModel = registerViewModel
        setPossibleUserTypes(binding)
        val registerDialog = buildGoogleSignupDialog(registerViewModel::registerGoogleUser,
            registerViewModel::cancelGoogleRegister, binding.root)
        registerDialog.show()
    }

    private fun setLogUserWithGoogleAfterRegistrationObserver(registerViewModel: RegisterViewModel){
        registerViewModel.logUserWithGoogle.observe(viewLifecycleOwner, Observer { logUser ->
            if (logUser){
                viewModel.onGoogleLogin(registerViewModel.googleToken.value!!)
                registerViewModel.onLogUserWithGoogleEnd()
            }
        })
    }

    private fun buildGoogleSignupDialog(onPositiveBtnClick: () -> Unit,
                                        onNegativeBtnClick: () -> Unit,
                                        view: View) : AlertDialog{
        val builder = AlertDialog.Builder(context)
        val registerDialog = builder
            .setPositiveButton(
                "Registrarse"
            ) { _: DialogInterface?, _: Int -> onPositiveBtnClick() }
            .setNegativeButton(
                "Cancelar"
            ) { _: DialogInterface?, _: Int -> onNegativeBtnClick() }
            .create()
        registerDialog.setView(view)
        return registerDialog
    }

    private fun setPossibleUserTypes(binding: DialogRegisterBinding) {
        val items = listOf(getString(R.string.user_type_huesped), getString(R.string.user_type_anfitrion))
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.userTypeTextField.editText as? AutoCompleteTextView)?.setText(getString(R.string.user_type_huesped), false) //Defaults to huesped
    }
}