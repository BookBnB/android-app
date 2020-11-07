package com.example.bookbnb.ui.publicaciones

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.PublicacionItemBinding
import com.example.bookbnb.models.Publicacion

class PublicacionRecyclerViewAdapter : ListAdapter<Publicacion, PublicacionRecyclerViewAdapter.PublicacionViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionRecyclerViewAdapter.PublicacionViewHolder {
        return PublicacionViewHolder(PublicacionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = getItem(position)
        holder.bind(publicacion)
    }

    class PublicacionViewHolder(private var binding: PublicacionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(publicacion: Publicacion) {
            binding.property = publicacion
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