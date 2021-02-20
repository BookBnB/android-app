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
import com.example.bookbnb.models.chat.FirebaseChat
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.utils.notifyObserver
import com.example.bookbnb.viewmodels.ChatViewModel
import com.example.bookbnb.viewmodels.ChatViewModelViewModelFactory
import com.google.firebase.database.*
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
        ViewModelProvider(this, ChatViewModelViewModelFactory(activity.application,
            requireArguments().getString("userHuespedId")!!,
            requireArguments().getString("userAnfitrionId")!!))
            .get(ChatViewModel::class.java)
    }

    private lateinit var binding: FragmentChatBinding

    private lateinit var childMessagesReference: DatabaseReference
    private var childListener: ChildEventListener? = null

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

        childMessagesReference = Firebase.database.reference.child("messages").child(viewModel.chatId)

        val childMessagesListener = object : ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                viewModel.messages.value!!.add(dataSnapshot.getValue<FirebaseChatMessage>()!!)
                viewModel.messages.notifyObserver()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        }
        childMessagesReference.addChildEventListener(childMessagesListener)
        this.childListener = childMessagesListener


        Firebase.database.reference.child("chats").child(viewModel.chatId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessMgr = SessionManager(requireContext())
                (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = if (sessMgr.isUserHost())
                    dataSnapshot.getValue<FirebaseChat>()!!.userHuespedName
                else
                    dataSnapshot.getValue<FirebaseChat>()!!.userAnfitrionName
            }
        })

        binding.messageRecyclerView.adapter = ChatMessagesRecyclerViewAdapter()

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        childListener?.let {
            childMessagesReference.removeEventListener(it)
        }
        hideKeyboard()
        // Clean up comments listener
        //adapter?.cleanupListener()
    }
}