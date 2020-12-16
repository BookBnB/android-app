package com.example.bookbnb.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.MainActivity
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentLoginBinding
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.LoginViewModel
import com.example.bookbnb.viewmodels.LoginViewModelFactory
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
            //val sessionManager = SessionManager(this)
            //sessionManager.saveUserId(account.id)
            //goToMainActivity()
        } else {
            //val sessionManager = SessionManager(this)
            //sessionManager.removeUserId()
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
        setSignUpWithGoogleObserver()
        setSpinnerObserver(viewModel, requireActivity().findViewById(R.id.spinner_holder))
        setSnackbarMessageObserver(viewModel, requireActivity().findViewById(R.id.login_activity_layout))

        return binding.root
    }

    private fun setSignUpWithGoogleObserver(){
        viewModel.showGoogleSignUp.observe(viewLifecycleOwner, Observer { show ->
            if (show){
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, REQ_ONE_TAP)
                viewModel.onDoneShowingGoogleSignUpClick()
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
            // TODO: Signed in successfully, send token to server to create user or log the user in.
            Log.w(TAG, account.idToken.toString())
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // TODO: Show error
        }
    }
}