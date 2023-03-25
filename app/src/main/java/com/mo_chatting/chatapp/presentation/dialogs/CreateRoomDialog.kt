package com.mo_chatting.chatapp.presentation.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.data.models.Room
import com.mo_chatting.chatapp.databinding.FragmentCreateRoomDialogBinding
import com.mo_chatting.chatapp.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateRoomDialog(val homeFragment: HomeFragment) : DialogFragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var binding: FragmentCreateRoomDialogBinding
    private var listener: MyDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateRoomDialogBinding.inflate(layoutInflater)
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
                this@CreateRoomDialog.dismiss()
            }

            btnCreateNewRoom.setOnClickListener {
                listener?.onDataPassed(
                    Room(
                        roomName = binding.etRoomName.text.toString(),
                        hasPassword = binding.checkboxPassword.isChecked,
                        password = binding.etPassword.text.toString(),
                        roomTypeImage = getRoomType(),
                        roomOwnerId = firebaseAuth.currentUser!!.uid
                    )
                )
               this@CreateRoomDialog.dismiss()
            }

            btnRoomType.setOnClickListener {

            }

        }
    }

    private fun getRoomType(): Int {
        // TODO: add logic
        return 0
    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 9)
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

interface MyDialogListener {
    fun onDataPassed(room: Room)
}
