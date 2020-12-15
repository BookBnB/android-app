package com.example.bookbnb.ui.reservas

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.ReservaItemBinding
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.models.Reserva

class ReservaRecyclerViewAdapter(diffCallback: DiffUtil.ItemCallback<Reserva>) :
    ListAdapter<Reserva, ReservaRecyclerViewAdapter.ReservaViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        return ReservaViewHolder(
            ReservaItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = getItem(position)
        holder.bind(reserva)
    }

    class ReservaViewHolder(private var binding: ReservaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reserva: Reserva) {
            binding.property = reserva
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