package com.example.bookbnb.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.R
import com.example.bookbnb.databinding.ReservaItemBinding
import com.example.bookbnb.models.Reserva

class ReservaRecyclerViewAdapter(val aceptarClickListener: ReservaListener,
                                 val rechazarClickListener: ReservaListener) :
    ListAdapter<Reserva, ReservaRecyclerViewAdapter.ReservaViewHolder>(
        DiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        return ReservaViewHolder(
            ReservaItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = getItem(position)
        holder.bind(reserva, aceptarClickListener, rechazarClickListener)
        if (reserva.isFinished()) {
            holder.itemView.let {
                it.background =
                    ResourcesCompat.getDrawable(it.resources, R.drawable.grayReservas, null)
                it.alpha = 0.5f
            }
        }
    }

    class ReservaViewHolder(private var binding: ReservaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reserva: Reserva, aceptarClickListener: ReservaListener, rechazarClickListener: ReservaListener) {
            binding.property = reserva
            binding.aceptarListener = aceptarClickListener
            binding.rechazarListener = rechazarClickListener
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

class ReservaListener(val clickListener: (reserva: Reserva) -> Unit) {
    fun onClick(reserva: Reserva) = clickListener(reserva)
}