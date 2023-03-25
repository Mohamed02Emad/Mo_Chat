package com.mo_chatting.chatapp.presentation.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentEnterPasswordDialogBinding
import com.mo_chatting.chatapp.presentation.home.HomeFragment
import kotlinx.coroutines.CoroutineScope

class EnterPasswordDialog(val homeFragment: HomeFragment,val room: Room) : DialogFragment() {

    private lateinit var binding: FragmentEnterPasswordDialogBinding
    private var listener: MyEnterPasswordListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterPasswordDialogBinding.inflate(layoutInflater)
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
                this@EnterPasswordDialog.dismiss()
            }

            btnSave.setOnClickListener {
              if (binding.etPassword.text.isNotEmpty()){
                  if (binding.etPassword.text.toString() == room.password){
                      listener!!.onPasswordReceive(room = room)
                      this@EnterPasswordDialog.dismiss()
                  }else{
                      Toast.makeText(requireContext(),"WrongPassword",Toast.LENGTH_LONG).show()
                  }
              }else{
                  Toast.makeText(requireContext(),"Empty password",Toast.LENGTH_LONG).show()
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

interface MyEnterPasswordListener{
    fun onPasswordReceive(room: Room)

}