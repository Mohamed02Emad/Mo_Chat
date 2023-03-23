package com.mo_chatting.chatapp.presentation.recyclerViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Message
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.MessageCardBinding

class ChatAdapter(
    private val list: ArrayList<Message>,
    private val onClickListener: OnChatClickListener,
    private val onLongClickListener: OnChatLongClickListener,
    private val userId : String
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
        holder.binding.messageBody.text = currentMessage.messageContent
        holder.binding.tvMessageDate.text = currentMessage.messageDate
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
                    onLongClickListener.onRoomLongClick(currentMessage, position)
                }
            }
        }
    }


    private fun setCardColors(
        holder: ChatAdapter.HomeViewHolder,
        currentMessage: Message,
        position: Int
    ) {

        if (currentMessage.messageOwner==userId){
            holder.binding.myParent.background = ContextCompat.getDrawable(
                holder.binding.myParent.context, R.drawable.my_message
            )
        }else{
            holder.binding.myParent.background = ContextCompat.getDrawable(
                holder.binding.myParent.context, R.drawable.their_message
            )
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    class OnChatClickListener(private val clickListener: (message: Message, position: Int) -> Unit) {
        fun onChatClick(message: Message, position: Int) = clickListener(message, position)
    }

    class OnChatLongClickListener(private val longClickListener: (message: Message, position: Int) -> Boolean) {
        fun onRoomLongClick(message: Message, position: Int) = longClickListener(message, position)
    }
}