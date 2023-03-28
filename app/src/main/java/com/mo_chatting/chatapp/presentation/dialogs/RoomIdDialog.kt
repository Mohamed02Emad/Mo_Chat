package com.mo_chatting.chatapp.presentation.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.mo_chatting.chatapp.databinding.FragmentRoomIdDialogBinding

class RoomIdDialog(val roomId: String) : DialogFragment() {
    private lateinit var binding: FragmentRoomIdDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomIdDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimentions()
        setViews()
        setOnClicks()
    }

    private fun setViews() {
        binding.apply {
            tvCode.text = " $roomId"
        }
    }

    private fun setOnClicks() {
        binding.apply {
            btnCancel.setOnClickListener {
                this@RoomIdDialog.dismiss()
            }

            btnCopy.setOnClickListener {
                copyIdToClipBoard(roomId)
                Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyIdToClipBoard(roomId: String) {
        // Get a reference to the system clipboard
        val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a new ClipData object with the text to be copied
        val clipData = ClipData.newPlainText("Room Id", roomId)

        // Set the ClipData object as the primary clip on the clipboard
        clipboardManager.setPrimaryClip(clipData)

    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 20)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}