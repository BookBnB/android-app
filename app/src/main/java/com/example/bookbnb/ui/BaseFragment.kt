package com.example.bookbnb.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bookbnb.R
import com.example.bookbnb.viewmodels.BaseAndroidViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    protected fun setSnackbarMessageObserver(viewModel: BaseAndroidViewModel, view: View) {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer { msg ->
            msg?.let {
                var snackbar = Snackbar.make(
                    view,
                    it,
                    Snackbar.LENGTH_LONG
                )
                snackbar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), viewModel.snackbarColor.value!!));
                snackbar.show()
                viewModel.onDoneShowingSnackbarMessage()
            }
        })
    }

    protected fun setSpinnerObserver(viewModel: BaseAndroidViewModel, spinnerHolder: View) {
        viewModel.showLoadingSpinner.observe(viewLifecycleOwner, Observer { show ->
            hideKeyboard()
            spinnerHolder.visibility = if (show) View.VISIBLE else View.GONE
        })
    }

    protected fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    protected fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}