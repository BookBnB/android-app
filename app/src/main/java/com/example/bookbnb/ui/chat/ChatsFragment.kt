package com.example.bookbnb.ui.chat

import android.os.Bundle
import android.se.omapi.Session
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.bookbnb.R
import com.example.bookbnb.adapters.ChatClickListener
import com.example.bookbnb.adapters.ChatsRecyclerViewAdapter
import com.example.bookbnb.databinding.FragmentChatsBinding
import com.example.bookbnb.models.chat.FirebaseChat
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.viewmodels.ChatsViewModel
import com.example.bookbnb.viewmodels.ChatsViewModelFactory
import com.example.bookbnb.viewmodels.FirebaseChatVM
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

/**
 * A fragment representing a list of Items.
 */
class ChatsFragment() : BaseFragment() {

    private val viewModel: ChatsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, ChatsViewModelFactory(activity.application))
            .get(ChatsViewModel::class.java)
    }

    private lateinit var binding: FragmentChatsBinding

    private lateinit var chatsReference: Query

    private var chatsListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chats,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setSnackbarMessageObserver(viewModel, binding.root)

        setChatsListAdapter()

        val sessionMgr = SessionManager(requireContext())
        val userId = sessionMgr.getUserId()
        chatsReference = if (sessionMgr.isUserHost()){
            Firebase.database.reference
                .child("chats").orderByChild("userAnfitrionId").startAt(userId).endAt(userId + "\uf8ff")
        } else{
            Firebase.database.reference
                .child("chats").orderByChild("userHuespedId").startAt(userId).endAt(userId + "\uf8ff")
        }
        val chatsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chats = dataSnapshot.getValue<HashMap<String, FirebaseChat>>()
                viewModel.chats.value = chats?.map{c ->
                    val chatTitle = if (sessionMgr.isUserHost()) c.value.userHuespedName else c.value.userAnfitrionName
                    FirebaseChatVM(c.value.chatId, chatTitle)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // [START_EXCLUDE]
                Toast.makeText(requireContext(), "Failed to load chats.",
                    Toast.LENGTH_SHORT).show()
                // [END_EXCLUDE]
            }
        }
        chatsReference.addValueEventListener(chatsListener)

        this.chatsListener = chatsListener

        return binding.root
    }

    private fun setChatsListAdapter() {
        binding.chatsList.adapter = ChatsRecyclerViewAdapter(ChatClickListener { chatId ->
            val userIds = FirebaseDBService().getUserIdsFromChatId(chatId)
            NavHostFragment.findNavController(this).navigate(
                ChatsFragmentDirections.actionChatsToChatFragment(userIds[0], userIds[1])
            )
        })
        binding.chatsList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onStop() {
        super.onStop()

        // Remove post value event listener
        chatsListener?.let {
            chatsReference.removeEventListener(it)
        }

    }
}