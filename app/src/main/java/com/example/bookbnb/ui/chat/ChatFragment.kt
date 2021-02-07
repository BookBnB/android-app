package com.example.bookbnb.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bookbnb.R
import com.example.bookbnb.databinding.FragmentChatBinding
import com.example.bookbnb.network.FirebaseDBService
import com.example.bookbnb.ui.BaseFragment
import com.example.bookbnb.viewmodels.ChatViewModel
import com.example.bookbnb.viewmodels.ChatViewModelViewModelFactory

class ChatFragment : BaseFragment() {
/*
    private val mFirebaseAdapter: FirebaseRecyclerAdapter<FirebaseChatMessage, MessageViewHolder>? =
        null
*/
    private val viewModel: ChatViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, ChatViewModelViewModelFactory(activity.application))
            .get(ChatViewModel::class.java)
    }

    private lateinit var binding: FragmentChatBinding

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

        val userHuespedId = requireArguments().getString("userHuespedId")!!
        val userAnfitrionId = requireArguments().getString("userAnfitrionId")!!
        val firebaseDbSvc = FirebaseDBService()
        firebaseDbSvc.updateChat(userHuespedId, userAnfitrionId)

        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = ""

        return binding.root
    }
}