package com.mo_chatting.chatapp.presentation.signUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.btnBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignUp.setOnClickListener {
            if (accountCreated()) {
                startActivity(Intent(requireActivity(),MainActivity::class.java))
            } else {
                //todo add loigc
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginWithFacebook.setOnClickListener {
            showToast("Soon")
        }
        binding.loginWithGoogle.setOnClickListener {
            showToast("Soon")
        }
    }

    private fun accountCreated(): Boolean {
        // TODO: add logic
        return true
    }

    private fun showToast(s:String)=
        Toast.makeText(requireActivity(),s,Toast.LENGTH_SHORT).show()


}