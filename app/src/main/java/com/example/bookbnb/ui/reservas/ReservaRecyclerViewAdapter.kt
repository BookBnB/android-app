package com.example.bookbnb.ui.reservas

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.ReservaItemBinding
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.ui.publicaciones.PublicacionListener

class ReservaRecyclerViewAdapter(val clickListener: ReservaListener) :
    ListAdapter<Reserva, ReservaRecyclerViewAdapter.ReservaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        return ReservaViewHolder(
            ReservaItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = getItem(position)
        holder.bind(reserva, clickListener)
    }

    class ReservaViewHolder(private var binding: ReservaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reserva: Reserva, clickListener: ReservaListener) {
            binding.property = reserva
            binding.aceptarListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reserva>() {
        override fun areItemsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class ReservaListener(val clickListener: (reservaId: String) -> Unit) {
    fun onClick(reserva: Reserva) = clickListener(reserva.id)
}