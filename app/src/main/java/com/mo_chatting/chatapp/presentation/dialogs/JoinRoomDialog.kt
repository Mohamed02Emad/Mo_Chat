package com.mo_chatting.chatapp.presentation.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mo_chatting.chatapp.databinding.FragmentJoinRoomDialogBinding
import com.mo_chatting.chatapp.presentation.groupChat.GroupChatFragment

class JoinRoomDialog(val homeFragment: GroupChatFragment) : DialogFragment() {

    private lateinit var binding: FragmentJoinRoomDialogBinding
    private var listener: DialogsInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinRoomDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimentions()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.apply {
            btnCancel.setOnClickListener {
                this@JoinRoomDialog.dismiss()
            }

            btnJoin.setOnClickListener {
                if (binding.etRoomId.text.toString().length == 8) {
                    listener!!.onDataPassedJoinRoom(binding.etRoomId.text.toString())
                    this@JoinRoomDialog.dismiss()
                }
            }
        }
    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 20)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = homeFragment
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyDialogListener")
        }
    }
}