package com.example.bookbnb.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.ChatMessageItemBinding
import com.example.bookbnb.databinding.PreguntaItemBinding
import com.example.bookbnb.models.Pregunta
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.google.firebase.ktx.Firebase

class ChatMessagesRecyclerViewAdapter() : ListAdapter<FirebaseChatMessage, ChatMessagesRecyclerViewAdapter.MessageViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesRecyclerViewAdapter.MessageViewHolder {
        return MessageViewHolder(
            ChatMessageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    class MessageViewHolder(private var binding: ChatMessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: FirebaseChatMessage) {
            binding.property = message
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FirebaseChatMessage>() {
        override fun areItemsTheSame(oldItem: FirebaseChatMessage, newItem: FirebaseChatMessage): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FirebaseChatMessage, newItem: FirebaseChatMessage): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }
    }
}
