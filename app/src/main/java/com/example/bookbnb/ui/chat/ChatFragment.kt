package com.example.bookbnb.ui.chat

import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.ChatMessagesRecyclerViewAdapter
import com.example.bookbnb.databinding.FragmentChatBinding
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.ChatViewModel
import com.example.bookbnb.viewmodels.ChatViewModelViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import kotlin.collections.HashMap

class ChatFragment : BaseFragment() {

    private val viewModel: ChatViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, ChatViewModelViewModelFactory(activity.application,
            requireArguments().getString("userHuespedId")!!,
            requireArguments().getString("userAnfitrionId")!!))
            .get(ChatViewModel::class.java)
    }

    private lateinit var binding: FragmentChatBinding

    private lateinit var messagesReference: DatabaseReference

    private var messagesListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.chatViewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)

        val firebaseDbSvc = FirebaseDBService()
        firebaseDbSvc.updateChat(viewModel.huespedId, viewModel.anfitrionId)

        messagesReference = Firebase.database.reference
            .child("messages").child(viewModel.chatId)

        val messagesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messages = dataSnapshot.getValue<HashMap<String, FirebaseChatMessage>>()
                viewModel.messages.value = messages?.map{m -> m.value }?.sortedBy { m -> Date(m.timestamp["time"] as Long) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // [START_EXCLUDE]
                Toast.makeText(requireContext(), "Failed to load post.",
                    Toast.LENGTH_SHORT).show()
                // [END_EXCLUDE]
            }
        }
        messagesReference.addValueEventListener(messagesListener)
        this.messagesListener = messagesListener

        binding.messageRecyclerView.adapter = ChatMessagesRecyclerViewAdapter()
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = viewModel.chatId

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        // Remove post value event listener
        messagesListener?.let {
            messagesReference.removeEventListener(it)
        }

        // Clean up comments listener
        //adapter?.cleanupListener()
    }
}