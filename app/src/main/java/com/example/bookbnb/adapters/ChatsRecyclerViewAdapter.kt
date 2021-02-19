package com.example.bookbnb.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookbnb.databinding.ChatItemBinding
import com.example.bookbnb.models.chat.FirebaseChat

class ChatsRecyclerViewAdapter(val clickListener: ChatClickListener? = null) : ListAdapter<FirebaseChat, ChatsRecyclerViewAdapter.ChatViewHolder>(DiffCallback)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsRecyclerViewAdapter.ChatViewHolder {
        return ChatsRecyclerViewAdapter.ChatViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ChatViewHolder(private var binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: FirebaseChat, clickListener: ChatClickListener?) {
            if (clickListener != null){
                binding.clickListener = clickListener
            }
            binding.property = message
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FirebaseChat>() {
        override fun areItemsTheSame(oldItem: FirebaseChat, newItem: FirebaseChat): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FirebaseChat, newItem: FirebaseChat): Boolean {
            return oldItem.chatId == newItem.chatId
        }
    }
}

class ChatClickListener(val clickListener: (chatId: String) -> Unit) {
    fun onClick(chat: FirebaseChat) = clickListener(chat.chatId!!)
}