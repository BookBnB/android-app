package com.example.bookbnb.adapters

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.PublicacionItemBinding
import com.example.bookbnb.databinding.RecomendacionItemBinding
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.utils.CustomImageUri
import com.example.bookbnb.utils.ImagesSliderAdapter

class RecomendacionesRecyclerViewAdapter(val clickListener: RecomendacionPublicacionListener, val loadImgsFromFireBase: Boolean = false)
    : ListAdapter<Publicacion, RecomendacionesRecyclerViewAdapter.PublicacionViewHolder>(
    DiffCallback
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        return PublicacionViewHolder(
            RecomendacionItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            loadImgsFromFireBase
        )
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = getItem(position)
        holder.bind(publicacion, clickListener)
    }

    class PublicacionViewHolder(private var binding: RecomendacionItemBinding, val loadImgsFromFireBase: Boolean = false) : RecyclerView.ViewHolder(binding.root) {
        fun bind(publicacion: Publicacion, clickListener: RecomendacionPublicacionListener) {
            binding.property = publicacion
            binding.clickListener = clickListener

            val adapter = ImagesSliderAdapter(binding.imageSlider.context, loadImgsFromFireBase)
            adapter.renewItems(publicacion.imagenes.map { CustomImageUri(Uri.parse(it.url)) }.toMutableList())
            binding.imageSlider.setSliderAdapter(adapter)
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Publicacion>() {
        override fun areItemsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class RecomendacionPublicacionListener(val clickListener: (publicacionId: String) -> Unit) {
    fun onClick(publicacion: Publicacion) = clickListener(publicacion.id!!)
}