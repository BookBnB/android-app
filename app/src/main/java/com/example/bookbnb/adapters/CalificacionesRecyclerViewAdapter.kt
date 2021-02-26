package com.example.bookbnb.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.CalificacionItemBinding
import com.example.bookbnb.databinding.PreguntaItemBinding
import com.example.bookbnb.models.Pregunta
import com.example.bookbnb.viewmodels.CalificacionVM

class CalificacionesRecyclerViewAdapter() : ListAdapter<CalificacionVM, CalificacionesRecyclerViewAdapter.CalificacionViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalificacionesRecyclerViewAdapter.CalificacionViewHolder {
        return CalificacionViewHolder(
            CalificacionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CalificacionViewHolder, position: Int) {
        val calificacionVM = getItem(position)
        holder.bind(calificacionVM)
    }

    class CalificacionViewHolder(private var binding: CalificacionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(calificacionVM: CalificacionVM) {
            binding.property = calificacionVM
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CalificacionVM>() {
        override fun areItemsTheSame(oldItem: CalificacionVM, newItem: CalificacionVM): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CalificacionVM, newItem: CalificacionVM): Boolean {
            return oldItem.puntos == newItem.puntos && oldItem.detalle == newItem.detalle
        }
    }
}
