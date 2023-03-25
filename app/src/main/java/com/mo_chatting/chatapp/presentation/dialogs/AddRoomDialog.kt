package com.mo_chatting.chatapp.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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
                Toast.makeText(requireContext(),"soon",Toast.LENGTH_LONG).show()
            }

            btnCreateNewRoom.setOnClickListener {
                val createRoomDialogBinding = CreateRoomDialog(homeFragment)
//                 TODO: remember to slove this
//                createRoomDialogBinding.setTargetFragment(parentFragment,0)
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