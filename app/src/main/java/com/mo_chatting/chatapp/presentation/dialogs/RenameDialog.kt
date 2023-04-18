package com.mo_chatting.chatapp.presentation.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.appClasses.validateUserName
import com.mo_chatting.chatapp.data.dataStore.DataStoreImpl
import com.mo_chatting.chatapp.databinding.FragmentRenameDialogBinding
import com.mo_chatting.chatapp.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RenameDialog(val homeFragment: HomeFragment) : DialogFragment() {
    @Inject
    lateinit var dataStore:DataStoreImpl

    private lateinit var binding: FragmentRenameDialogBinding
    private var newName = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private var listener: DialogsInterface? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRenameDialogBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimentions()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.apply {
            editText.doAfterTextChanged {
                val name = validateUserName(it.toString())
                if (name.isValid) {
                    newName = it.toString()
                } else {
                    newName = ""
                }
            }

            btnCancel.setOnClickListener {
                this@RenameDialog.dismiss()
            }

            binding.btnSave.apply {
                setOnClickListener {
                    startAnimation {
                        lifecycleScope.launch {
                            updateUserName()
                            revertAnimation()
                        }
                    }
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

    private suspend fun updateUserName() {
        val name = validateUserName(newName)
        if (!name.isValid) {
            Toast.makeText(requireContext(), name.message, Toast.LENGTH_LONG).show()
            return
        }
        listener!!.onDataPassedRename(newName)
        this@RenameDialog.dismiss()
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
