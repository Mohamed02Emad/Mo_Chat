package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.MessageType
import com.mo_chatting.chatapp.databinding.MyMessageCardBinding
import com.mo_chatting.chatapp.databinding.TheirMessageCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatAdapter(
    private val onClickListener: OnChatClickListener,
    private val userId: String
) :
    PagingDataAdapter<Message, ChatAdapter.BaseViewHolder>(Companion) {

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Message, position: Int)
    }

    inner class MyMessageViewHolder(val binding: MyMessageCardBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(currentMessage: Message, position: Int) {

            if (currentMessage.messageType == MessageType.TEXT) {
                binding.apply {
                    messageBody.text = currentMessage.messageText
                    messageBody.visibility = View.VISIBLE
                    messageImage.visibility = View.GONE
                }
            } else {
                binding.apply {
                    messageBody.visibility = View.GONE
                    messageImage.visibility = View.VISIBLE
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(binding.messageImage)
                        .load(getMessageImage(currentMessage.messageImage))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.messageImage)
                }
            }

            binding.tvMessageDate.text = currentMessage.messageDateAndTime
            binding.tvMessageOwner.text = currentMessage.messageOwner

            binding.apply {
                myParent.apply {
                    setOnClickListener {
                        onClickListener.onChatClick(currentMessage, position)
                    }

                    setOnLongClickListener {
                        onClickListener.onRoomLongClick(currentMessage, position)
                    }
                }

                tvMessageOwner.setOnClickListener {
                    if (currentMessage.messageOwnerId != "firebase"){
                        onClickListener.onUserNameClicked(
                            currentMessage.messageOwnerId,
                            currentMessage.messageOwner
                        )
                    }
                }

                messageImage.setOnClickListener {
                    onClickListener.onImageClicked(currentMessage.messageImage)
                }
            }
        }
    }

    inner class TheirMessageViewHolder(val binding: TheirMessageCardBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(currentMessage: Message ,  position: Int) {

            if (currentMessage.messageType == MessageType.TEXT) {
                binding.apply {
                    messageBody.text = currentMessage.messageText
                    messageBody.visibility = View.VISIBLE
                    messageImage.visibility = View.GONE
                }
            } else {
                binding.apply {
                    messageBody.visibility = View.GONE
                    messageImage.visibility = View.VISIBLE
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(binding.messageImage)
                        .load(getMessageImage(currentMessage.messageImage))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.messageImage)
                }
            }

            binding.tvMessageDate.text = currentMessage.messageDateAndTime
            binding.tvMessageOwner.text = currentMessage.messageOwner

                binding.apply {
                myParent.apply {
                    setOnClickListener {
                        onClickListener.onChatClick(currentMessage, position)
                    }

                    setOnLongClickListener {
                        onClickListener.onRoomLongClick(currentMessage, position)
                    }
                }

                tvMessageOwner.setOnClickListener {
                    if (currentMessage.messageOwnerId != "firebase"){
                        onClickListener.onUserNameClicked(
                            currentMessage.messageOwnerId,
                            currentMessage.messageOwner
                        )
                    }
                }

                messageImage.setOnClickListener {
                    onClickListener.onImageClicked(currentMessage.messageImage)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.BaseViewHolder {
        return if (viewType == 0){
            MyMessageViewHolder(
                MyMessageCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            TheirMessageViewHolder(
                TheirMessageCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: ChatAdapter.BaseViewHolder, position: Int) {
        holder.bind(getItem(position)!!,position)
    }



    private suspend fun getMessageImage(messageImage: String?): Uri? {
        var uriToReturn: Uri? = null
        try {

            val storageRef = FirebaseStorage.getInstance()
                .getReference(messageImage.toString())
            storageRef.downloadUrl.apply {
                addOnSuccessListener { downloadUri ->
                    uriToReturn = downloadUri
                }
                await()
            }
        } catch (_: Exception) {
        }
        return uriToReturn
    }



    fun getMessageAt(position: Int): Message? {
        return try {
            getItem(position)
        } catch (_: Exception) {
            null
        }
    }

    fun getMessagesList(): List<Message> {
        val size = itemCount
        if (size == 0) return emptyList()
        val list = ArrayList<Message>(size)
        for (i in 0 until size) {
            list.add(getItem(i)!!)
        }
        return list
    }

    fun getMessageIndex(message: Message): Int {
        val size = itemCount
        if (size == 0) return -1
        for (i in 0 until size) {
            if (getItem(i) == message) return i
        }
        return -1
    }

    override fun getItemViewType(position: Int): Int {
        // return 0 for my message and 1 for their message
        return when (getItem(position)!!.messageOwnerId) {
            userId -> 0
            else -> 1
        }
    }


    class OnChatClickListener(
        private val clickListener: (message: Message, position: Int) -> Unit,
        private val longClickListener: (message: Message, position: Int) -> Boolean,
        private val userNameClickListener: (userId: String, userName: String) -> Unit,
        private val ImageClicked: (messageImage: String?) -> Unit
    ) {
        fun onChatClick(message: Message, position: Int) = clickListener(message, position)
        fun onRoomLongClick(message: Message, position: Int) = longClickListener(message, position)
        fun onUserNameClicked(userId: String, userName: String) =
            userNameClickListener(userId, userName)

        fun onImageClicked(messageImage: String?) = ImageClicked(messageImage)

    }

    companion object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (oldItem.timeWithMillis == newItem.timeWithMillis) && (oldItem.messageOwnerId == newItem.messageOwnerId)
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}


