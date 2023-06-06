package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.DirectContact
import com.mo_chatting.chatapp.databinding.RoomCardBinding

class DirectChatsAdapter(
    private val list: ArrayList<DirectContact>,
    private val onClickListener: OnChatClickListener,
    private val currentUserName: String
) :
    RecyclerView.Adapter<DirectChatsAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: RoomCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            RoomCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentChat = list[position]
        holder.binding.apply {
            ivRoomType.visibility = View.GONE

            val chatName = if (currentChat.user1 == currentUserName) {
                setImage(currentChat.user2Image, holder)
                currentChat.user2
            } else {
                setImage(currentChat.user1Image, holder)
                currentChat.user1
            }
            tvRoomName.text = chatName.trimEnd().trimStart()
            tvLastMessage.text = ""
        }
        setCardOnClicks(holder, currentChat, position)
    }

    private fun setImage(imgUrl: String, holder: HomeViewHolder) {
        val img: Uri? = Uri.parse(imgUrl)
        Glide.with(holder.binding.roomTypeBackground)
            .load(img)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .override(200, 200)
            .centerCrop()
            .into(holder.binding.roomTypeBackground)
    }

    private fun setCardOnClicks(
        holder: DirectChatsAdapter.HomeViewHolder,
        currentChat: DirectContact,
        position: Int
    ) {
        holder.binding.apply {
            card.apply {
                setOnClickListener {
                    onClickListener.onRoomClick(currentChat, position)
                }
                setOnLongClickListener {
                    onClickListener.onRoomLongClick(currentChat, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItemByPosition(adapterPosition: Int): DirectContact {
        return list[adapterPosition]
    }

    class OnChatClickListener(
        private val clickListener: (chat: DirectContact, position: Int) -> Unit,
        private val longClickListener: (chat: DirectContact, position: Int) -> Boolean,
        private val deleteClickListener: (chat: DirectContact, position: Int) -> Unit,
        private val editClickListener: (chat: DirectContact, position: Int) -> Unit,
        private val pinClickListener: (chat: DirectContact, position: Int) -> Unit

    ) {
        fun onRoomClick(chat: DirectContact, position: Int) = clickListener(chat, position)

        fun onRoomLongClick(chat: DirectContact, position: Int) = longClickListener(chat, position)

        fun deleteRoom(chat: DirectContact, position: Int) = deleteClickListener(chat, position)

        fun editRoom(chat: DirectContact, position: Int) = editClickListener(chat, position)

        fun onPinRoom(chat: DirectContact, position: Int) = pinClickListener(chat, position)

    }

}