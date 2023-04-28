package com.mo_chatting.chatapp.presentation.recyclerViews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.RoomCardBinding

class HomeRoomAdapter(
    private val list: ArrayList<Room>,
    val uId: String,
    private val onClickListener: OnRoomClickListener,
) :
    RecyclerView.Adapter<HomeRoomAdapter.HomeViewHolder>() {

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
        val currentRoom = list[position]
        holder.binding.apply {
            tvRoomName.text = currentRoom.roomName
            tvLastMessage.text = currentRoom.lastMessage
        }

        setRoomType(holder, currentRoom.roomTypeImage)
        setCardOnClicks(holder, currentRoom, position)
    }

    private fun setCardOnClicks(
        holder: HomeRoomAdapter.HomeViewHolder,
        currentRoom: Room,
        position: Int
    ) {
        holder.binding.apply {
            card.apply {
                setOnClickListener {
                    onClickListener.onRoomClick(currentRoom, position)
                }

                setOnLongClickListener {
                    onClickListener.onRoomLongClick(currentRoom, position)
                }
            }
//            delete.setOnClickListener {
//                onClickListener.deleteRoom(currentRoom, position)
//            }
//
//            edit.setOnClickListener {
//                onClickListener.editRoom(currentRoom, position)
//            }
//
//            pin.setOnClickListener {
//                onClickListener.onPinRoom(currentRoom,position)
//            }
        }
    }

    private fun setRoomType(holder: HomeRoomAdapter.HomeViewHolder, roomType: Int) {
        val image = when (roomType) {
            0 -> {
                R.drawable.ic_family
            }
            1 -> {
                R.drawable.ic_technology
            }
            2 -> {
                R.drawable.ic_talk
            }
            3 -> {
                R.drawable.ic_study
            }
            4 -> {
                R.drawable.ic_sport

            }
            5 -> {
                R.drawable.ic_heart
            }
            6 -> {
                R.drawable.ic_games
            }
            else -> {
                R.drawable.ic_food
            }
        }
        holder.binding.ivRoomType.setImageResource(image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItemByPosition(adapterPosition: Int): Room {
        return list[adapterPosition]
    }

    class OnRoomClickListener(
        private val clickListener: (room: Room, position: Int) -> Unit,
        private val longClickListener: (room: Room, position: Int) -> Boolean,
        private val deleteClickListener: (room: Room, position: Int) -> Unit,
        private val editClickListener: (room: Room, position: Int) -> Unit,
        private val pinClickListener: (room: Room, position: Int) -> Unit

    ) {
        fun onRoomClick(room: Room, position: Int) = clickListener(room, position)

        fun onRoomLongClick(room: Room, position: Int) = longClickListener(room, position)

        fun deleteRoom(room: Room, position: Int) = deleteClickListener(room, position)

        fun editRoom(room: Room, position: Int) = editClickListener(room, position)

        fun onPinRoom(room:Room,position: Int) = pinClickListener(room, position)

    }

}