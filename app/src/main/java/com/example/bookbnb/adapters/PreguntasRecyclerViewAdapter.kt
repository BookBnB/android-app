package com.example.bookbnb.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.PreguntaItemBinding
import com.example.bookbnb.models.Pregunta

class PreguntasRecyclerViewAdapter(val responderListener: ResponderPreguntaListener? = null) : ListAdapter<Pregunta, PreguntasRecyclerViewAdapter.PreguntaViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntasRecyclerViewAdapter.PreguntaViewHolder {
        return PreguntaViewHolder(
            PreguntaItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val publicacion = getItem(position)
        holder.bind(publicacion, responderListener)
    }

    class PreguntaViewHolder(private var binding: PreguntaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pregunta: Pregunta, responderListener: ResponderPreguntaListener?) {
            if (responderListener != null){
                binding.responderListener = responderListener
            }
            binding.property = pregunta
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Pregunta>() {
        override fun areItemsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class ResponderPreguntaListener(val clickListener: (preguntaId: String) -> Unit) {
    fun onClick(pregunta: Pregunta) = clickListener(pregunta.id!!)
}
