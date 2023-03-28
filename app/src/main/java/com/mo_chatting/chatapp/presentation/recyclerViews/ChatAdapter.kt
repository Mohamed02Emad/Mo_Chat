package com.mo_chatting.chatapp.presentation.recyclerViews

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.databinding.MessageCardBinding

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
        holder.binding.messageBody.text = currentMessage.messageText
        holder.binding.tvMessageDate.text = currentMessage.messageDateAndTime
        holder.binding.tvMessageOwner.text = currentMessage.messageOwner
        setCardColors(holder, currentMessage, position)
        setCardOnClicks(holder, currentMessage, position)
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
        private val userNameClickListener: (userId: String, userName: String) -> Unit
    ) {
        fun onChatClick(message: Message, position: Int) = clickListener(message, position)
        fun onRoomLongClick(message: Message, position: Int) = longClickListener(message, position)
        fun onUserNameClicked(userId: String, userName: String) =
            userNameClickListener(userId, userName)

    }

}