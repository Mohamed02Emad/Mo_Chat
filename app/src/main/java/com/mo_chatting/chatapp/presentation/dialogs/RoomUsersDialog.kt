package com.mo_chatting.chatapp.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentRoomUsersDialogBinding
import com.mo_chatting.chatapp.presentation.recyclerViews.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class RoomUsersDialog(val thisRoom: Room) : DialogFragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentRoomUsersDialogBinding
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomUsersDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimentions()
        CoroutineScope(Dispatchers.Main).launch {
            setupRecyclerView()
        }
    }

    private suspend fun setupRecyclerView() {
        adapter = UsersAdapter(generateList())
        binding.rvUsers.adapter = adapter
        binding.rvUsers.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
    }

    private suspend fun generateList(): ArrayList<Pair<Uri?, String>> {
        val userNames = thisRoom.listOFUsersNames
        val userId = thisRoom.listOFUsers
        val listToReturn = ArrayList<Pair<Uri?, String>>()
        for (i in 0 until userId.size) {
            val uri: Uri? = getUserImage(userId[i])
            val name = userNames[i]
            listToReturn.add(uri to name)

        }
        return listToReturn
    }

    private suspend fun getUserImage(userId: String): Uri? {
        var uriToReturn: Uri? = null
        try {
            val storageRef = FirebaseStorage.getInstance()
                .getReference("user_images/${userId}")
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

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 12)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}