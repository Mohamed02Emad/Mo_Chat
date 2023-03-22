package com.mo_chatting.chatapp.presentation.recyclerViews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.RoomCardBinding

class HomeRoomAdapter(private val list: ArrayList<Room>) :
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
    }

    private fun setCardColors(holder: HomeRoomAdapter.HomeViewHolder, position: Int) {
        if (position % 3 == 0) {
            holder.binding.roomTypeBackground.background= ContextCompat.getDrawable(holder.binding.roomTypeBackground.context
                , R.color.blue_50
            )
            holder.binding.card.background= ContextCompat.getDrawable(holder.binding.card.context
                , R.color.card_blue
            )
        } else if (position % 3 == 2) {
            holder.binding.roomTypeBackground.background= ContextCompat.getDrawable(holder.binding.roomTypeBackground.context
                , R.color.yellow
            )
            holder.binding.card.background= ContextCompat.getDrawable(holder.binding.card.context
                , R.color.yellow2
            )
        } else {
            holder.binding.roomTypeBackground.background= ContextCompat.getDrawable(holder.binding.roomTypeBackground.context
                , R.color.red_50
            )
            holder.binding.card.background= ContextCompat.getDrawable(holder.binding.card.context
                , R.color.red
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
}