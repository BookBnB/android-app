package com.example.bookbnb.ui.busqueda

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentBusquedaLocationBinding
import com.example.bookbnb.viewmodels.BusquedaViewModel
import com.example.bookbnb.viewmodels.BusquedaViewModelFactory

class BusquedaLocationFragment : Fragment() {

    companion object {
        private const val MIN_CHARS_TO_SUGGEST_LOCATION = 3
    }
    private val viewModel: BusquedaViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, BusquedaViewModelFactory(activity.application))
            .get(BusquedaViewModel::class.java)
    }

    private lateinit var binding: FragmentBusquedaLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_busqueda_location,
            container,
            false
        )

        binding.busquedaViewModel = viewModel
        binding.lifecycleOwner = this

        setLocationTextObserver()
        setSuggestionOnClick()

        viewModel.navigateToSearchResults.observe(viewLifecycleOwner, Observer {
            if (it){
                NavHostFragment.findNavController(this).navigate(
                    BusquedaLocationFragmentDirections.actionBusquedaLocationFragmentToResultadosBusquedaFragment(viewModel.publicaciones)
                )
                viewModel.onDoneNavigateToSearchResults()
            }
        })

        return binding.root
    }

    private fun setSuggestionOnClick() {
        binding.searchText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                val item = viewModel.autocompleteLocationAdapter.value?.getItem(pos)
                viewModel.setSelectedLocation(item)
                binding.searchText.setSelection(0) //Focus on the start of the TextView
                hideKeyboard()
            }
    }

    private fun setLocationTextObserver() {
        viewModel.destino.observe(viewLifecycleOwner, Observer { location ->
            if (location.length >= MIN_CHARS_TO_SUGGEST_LOCATION) {
                viewModel.searchLocation(location)
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