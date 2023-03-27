package com.mo_chatting.chatapp.presentation.recyclerViews

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
    val uId : String,
    private val onClickListener: OnRoomClickListener,
    private val onLongClickListener: OnLongClickListener,
    private val onRoomDeleteClickListener: OnRoomDeleteClickListener,
    private val onRoomEditClickListener: OnRoomEditClickListener
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
        holder.binding.tvRoomName.text = currentRoom.roomName
        setRoomType(holder, currentRoom.roomTypeImage)
        setCardColors(holder, position)
        setCardOnClicks(holder, currentRoom, position)
        hideUnNecessaryItems(holder, currentRoom)
    }

    private fun hideUnNecessaryItems(holder: HomeViewHolder, currentRoom: Room) {
        if (uId != currentRoom.roomOwnerId)
            holder.binding.apply {
                edit.visibility= View.GONE
                editIcon.visibility= View.GONE
            }
    }

    private fun setCardOnClicks(
        holder: HomeRoomAdapter.HomeViewHolder,
        currentRoom: Room,
        position: Int
    ) {
        holder.binding.apply {
            card.apply {
                setOnClickListener {
                    onClickListener.onRoomClick(currentRoom,position)
                }

                setOnLongClickListener {
                  onLongClickListener.onRoomLongClick(currentRoom,position)
                }
            }
            delete.setOnClickListener {
                onRoomDeleteClickListener.deleteRoom(currentRoom,position)
            }

            edit.setOnClickListener {
                onRoomEditClickListener.editRoom(currentRoom,position)
            }
        }
    }


    private fun setCardColors(holder: HomeRoomAdapter.HomeViewHolder, position: Int) {
        if (position % 3 == 0) {
            holder.binding.roomTypeBackground.background = ContextCompat.getDrawable(
                holder.binding.roomTypeBackground.context, R.color.blue_50
            )
            holder.binding.card.background = ContextCompat.getDrawable(
                holder.binding.card.context, R.drawable.blue_card_ripples
            )
        } else if (position % 3 == 2) {
            holder.binding.roomTypeBackground.background = ContextCompat.getDrawable(
                holder.binding.roomTypeBackground.context, R.color.yellow
            )
            holder.binding.card.background = ContextCompat.getDrawable(
                holder.binding.card.context, R.drawable.yellow_card_ripples
            )
        } else {
            holder.binding.roomTypeBackground.background = ContextCompat.getDrawable(
                holder.binding.roomTypeBackground.context, R.color.red_50
            )
            holder.binding.card.background = ContextCompat.getDrawable(
                holder.binding.card.context, R.drawable.red_card_ripples
            )
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
            else -> {
                R.drawable.ic_food
            }
        }
        holder.binding.ivRoomType.setImageResource(image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class OnRoomClickListener(private val clickListener: (room: Room, position: Int) -> Unit) {
        fun onRoomClick(room: Room, position: Int) = clickListener(room, position)
    }

    class OnLongClickListener(private val longClickListener: (room: Room, position: Int) -> Boolean) {
        fun onRoomLongClick(room: Room, position: Int) = longClickListener(room, position)
    }
    class OnRoomDeleteClickListener(private val deleteClickListener: (room: Room, position: Int) -> Unit) {
        fun deleteRoom(room: Room, position: Int) = deleteClickListener(room, position)
    }

    class OnRoomEditClickListener(private val editClickListener : (room:Room , position:Int)->Unit){
        fun editRoom(room: Room,position: Int) = editClickListener(room,position)
    }

}