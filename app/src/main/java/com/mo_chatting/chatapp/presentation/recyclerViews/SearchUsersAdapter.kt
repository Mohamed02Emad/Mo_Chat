package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.User
import com.mo_chatting.chatapp.databinding.SearchUserCardBinding
import java.net.URL

class SearchUsersAdapter(
    private val list: ArrayList<User>,
    private val onClickListener: OnUserClickListener,
) :
    RecyclerView.Adapter<SearchUsersAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: SearchUserCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            SearchUserCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentUser = list[position]
        holder.binding.apply {
            val userName = currentUser.userName.trimEnd().trimStart()
            tvUserName.text = userName
        }
        setUserImage(currentUser.imageUrl, holder)
        setCardOnClicks(holder, currentUser, position)
    }

    private fun setUserImage(imageUrl: String, holder: HomeViewHolder) {
        val img =  Uri.parse(imageUrl)
            Glide.with(holder.binding.ivUserImage)
                .load(img)
                .override(100, 80)
                .into(holder.binding.ivUserImage)
    }

    private fun setCardOnClicks(
        holder: SearchUsersAdapter.HomeViewHolder,
        currentUser: User,
        position: Int
    ) {
        holder.binding.apply {
            btnAdd.apply {
                setOnClickListener {
                    onClickListener.onAddClick(currentUser, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItemByPosition(adapterPosition: Int): User {
        return list[adapterPosition]
    }


    class OnUserClickListener(
        private val clickListener: (user: User, position: Int) -> Unit

    ) {
        fun onAddClick(user: User, position: Int) = clickListener(user, position)
    }
}


