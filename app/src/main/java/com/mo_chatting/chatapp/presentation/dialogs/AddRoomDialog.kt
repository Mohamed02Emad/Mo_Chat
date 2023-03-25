package com.mo_chatting.chatapp.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.createViewModelLazy
import com.mo_chatting.chatapp.databinding.FragmentAddRoomDialogBinding
import com.mo_chatting.chatapp.presentation.home.HomeFragment

class AddRoomDialog(val homeFragment: HomeFragment) : DialogFragment() {

    private lateinit var binding: FragmentAddRoomDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRoomDialogBinding.inflate(layoutInflater)
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
                this@AddRoomDialog.dismiss()
            }

            btnJoin.setOnClickListener {
                val joinRoomDialog=JoinRoomDialog(homeFragment)
                joinRoomDialog.show(requireActivity().supportFragmentManager,null)
                this@AddRoomDialog.dismiss()
            }

            btnCreateNewRoom.setOnClickListener {
                val createRoomDialogBinding = CreateRoomDialog(homeFragment)
                createRoomDialogBinding.show(requireActivity().supportFragmentManager,null)
                this@AddRoomDialog.dismiss()
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

}