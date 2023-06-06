package com.mo_chatting.chatapp.presentation.recyclerViews

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.data.models.User
import com.mo_chatting.chatapp.databinding.SearchUserCardBinding

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
        val img: Uri? = Uri.parse(imageUrl)
        Glide.with(holder.binding.ivUserImage)
            .load(img)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .override(150, 150)
            .centerCrop()
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
                    val btnText = holder.binding.btnAdd.text.toString()
                    val stringAdd = resources.getString(R.string.add)
                    val stringUndo = resources.getString(R.string.undo)
                    onClickListener.onAddClick(currentUser, position, btnText)
                    if (btnText == stringAdd) {
                        holder.binding.btnAdd.text = stringUndo
                    } else {
                        holder.binding.btnAdd.text = stringAdd
                    }
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
        private val clickListener: (user: User, position: Int, btnText: String) -> Unit

    ) {
        fun onAddClick(user: User, position: Int, btnText: String) =
            clickListener(user, position, btnText)
    }
}


