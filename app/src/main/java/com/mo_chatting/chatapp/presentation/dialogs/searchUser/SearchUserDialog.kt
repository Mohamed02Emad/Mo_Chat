package com.mo_chatting.chatapp.presentation.dialogs.searchUser

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mo_chatting.chatapp.data.models.User
import com.mo_chatting.chatapp.databinding.FragmentSearchUserDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchUserDialog : DialogFragment() {

    private lateinit var binding: FragmentSearchUserDialogBinding
    private val viewModel: SearchUserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUserDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimentions()
        setOnClicks()
        setObservers()
    }

    private fun setOnClicks() {
        binding.etSearch.doAfterTextChanged {
            it.toString().let {
                if (it.length > 4) {
                    lifecycleScope.launch {
                        viewModel.getUsersById(it)
                    }
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            setUpRecyclerView(users)
        }
    }

    private fun setUpRecyclerView(users: ArrayList<User>) {
        Toast.makeText(requireContext(), users.size.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun setDimentions() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        this.dialog!!.window!!.setLayout(((9 * width) / 10), (7 * height) / 10)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}