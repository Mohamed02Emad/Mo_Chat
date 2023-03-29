package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.MessageType
import com.mo_chatting.chatapp.databinding.MessageCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatAdapter(
    private val list: ArrayList<Message>,
    private val onClickListener: OnChatClickListener,
    private val userId: String
) :
    RecyclerView.Adapter<ChatAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: MessageCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            MessageCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentMessage = list[position]
        if (currentMessage.messageType==MessageType.TEXT){
            holder.binding.apply {
                messageBody.text = currentMessage.messageText
                messageBody.visibility=View.VISIBLE
                messageImage.visibility=View.GONE
            }
        }else{
            holder.binding.apply {
                messageBody.visibility=View.GONE
                messageImage.visibility=View.VISIBLE
            }
            CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(holder.binding.messageImage)
                        .load(getMessageImage(currentMessage.messageImage))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.binding.messageImage)
            }

        }
        holder.binding.tvMessageDate.text = currentMessage.messageDateAndTime
        holder.binding.tvMessageOwner.text = currentMessage.messageOwner
        setCardColors(holder, currentMessage, position)
        setCardOnClicks(holder, currentMessage, position)
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

    private fun setCardOnClicks(
        holder: ChatAdapter.HomeViewHolder,
        currentMessage: Message,
        position: Int
    ) {
        holder.binding.apply {
            myParent.apply {
                setOnClickListener {
                    onClickListener.onChatClick(currentMessage, position)
                }

                setOnLongClickListener {
                    onClickListener.onRoomLongClick(currentMessage, position)
                }
            }

            tvMessageOwner.setOnClickListener {
                if (currentMessage.messageOwnerId == userId || currentMessage.messageOwnerId == "firebase") {

                } else {
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


    private fun setCardColors(
        holder: ChatAdapter.HomeViewHolder,
        currentMessage: Message,
        position: Int
    ) {

        val myParentView = holder.binding.myParent

        if (currentMessage.messageOwnerId == userId) {
            myParentView.background = ContextCompat.getDrawable(
                myParentView.context, R.drawable.my_message
            )
            holder.binding.apply {
                view1.visibility = View.VISIBLE
                view2.visibility = View.GONE
                tvMessageOwner.setTextColor(
                    ContextCompat.getColor(
                        myParentView.context,
                        R.color.their_message_color
                    )
                )
                messageBody.setTextColor(
                    ContextCompat.getColor(
                        myParentView.context,
                        R.color.black
                    )
                )
                messageBody.gravity = Gravity.START
            }
            val params = myParentView.layoutParams as LinearLayout.LayoutParams
            params.setMargins(150, 0, 0, 0)
            myParentView.layoutParams = params

        } else {
            myParentView.background =
                ContextCompat.getDrawable(myParentView.context, R.drawable.their_message)
            holder.binding.apply {
                view1.visibility = View.GONE
                view2.visibility = View.VISIBLE
                tvMessageOwner.setTextColor(
                    ContextCompat.getColor(
                        myParentView.context,
                        R.color.blue_white
                    )
                )
                messageBody.setTextColor(
                    ContextCompat.getColor(
                        myParentView.context,
                        R.color.grey_dark
                    )
                )
                messageBody.gravity = Gravity.END

            }
            val params = myParentView.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, 0, 150, 0)
            myParentView.layoutParams = params
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    class OnChatClickListener(
        private val clickListener: (message: Message, position: Int) -> Unit,
        private val longClickListener: (message: Message, position: Int) -> Boolean,
        private val userNameClickListener: (userId: String, userName: String) -> Unit,
        private val ImageClicked : (messageImage:String?)->Unit
    ) {
        fun onChatClick(message: Message, position: Int) = clickListener(message, position)
        fun onRoomLongClick(message: Message, position: Int) = longClickListener(message, position)
        fun onUserNameClicked(userId: String, userName: String) =
            userNameClickListener(userId, userName)
        fun onImageClicked(messageImage: String?) = ImageClicked(messageImage)

    }

}