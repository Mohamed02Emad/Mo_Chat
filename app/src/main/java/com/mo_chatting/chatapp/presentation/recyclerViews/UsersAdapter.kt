package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.databinding.UserCardBinding

class UsersAdapter(
    private val list: ArrayList<Pair<Uri?, String>>
) :
    RecyclerView.Adapter<UsersAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: UserCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            UserCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentUser = list[position]
        Glide.with(holder.binding.userImage)
            .load(currentUser.first)
            .error(R.drawable.ic_profile)
            .override(500, 400)
            .into(holder.binding.userImage)
        holder.binding.userName.text = currentUser.second
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class OnImageClickListener(
        private val clickListener: (uri: Uri?, position: Int) -> Unit
    ) {
        fun onImageClick(uri: Uri?, position: Int) = clickListener(uri, position)
    }

}