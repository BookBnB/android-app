package com.example.bookbnb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbnb.databinding.HuespedReservaItemBinding
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.viewmodels.ReservaVM


class HuespedReservasRecyclerViewAdapter(val clickListener: ReservaVMListener) :
    ListAdapter<ReservaVM, HuespedReservasRecyclerViewAdapter.HuespedReservaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HuespedReservaViewHolder {
        return HuespedReservaViewHolder(
            HuespedReservaItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HuespedReservaViewHolder, position: Int) {
        val reservaVM = getItem(position)
        holder.bind(reservaVM, clickListener)
    }

    class HuespedReservaViewHolder(private var binding: HuespedReservaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reservaVM: ReservaVM, clickListener: ReservaVMListener) {
            binding.property = reservaVM
            binding.calificarListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ReservaVM>() {
        override fun areItemsTheSame(oldItem: ReservaVM, newItem: ReservaVM): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ReservaVM, newItem: ReservaVM): Boolean {
            return oldItem.reserva.id == newItem.reserva.id
        }
    }
}

class ReservaVMListener(val clickListener: (reservaId: ReservaVM) -> Unit) {
    fun onClick(reservaVM: ReservaVM) = clickListener(reservaVM)
}