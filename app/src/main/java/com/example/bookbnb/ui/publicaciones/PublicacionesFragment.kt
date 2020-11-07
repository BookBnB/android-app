package com.example.bookbnb.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentPublicacionesListBinding
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.viewmodels.PublicacionesViewModel


/**
 * A fragment representing a list of Items.
 */
class PublicacionesFragment : Fragment() {

    private val viewModel: PublicacionesViewModel by lazy {
        ViewModelProvider(this).get(PublicacionesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPublicacionesListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.publicacionesList.adapter = PublicacionRecyclerViewAdapter() as PublicacionRecyclerViewAdapter
        binding.publicacionesList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PublicacionesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}