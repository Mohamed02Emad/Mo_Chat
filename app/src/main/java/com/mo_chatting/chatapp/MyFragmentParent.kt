package com.mo_chatting.chatapp

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mo_chatting.chatapp.presentation.MainActivity.MainActivity

open class MyFragmentParent : Fragment() {
    fun showToast(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_LONG).show()
    }

    fun restart() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        requireContext().startActivity(intent)
        requireActivity().finishAffinity()
        requireActivity().overridePendingTransition(0, 0)
    }
}