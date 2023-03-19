package com.mo_chatting.chatapp.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var binding:FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.btnLogin.setOnClickListener {
            if (isValidAccount()){
                startActivity(Intent(requireActivity(),MainActivity::class.java))
                requireActivity().finish()
            }else{
                //todo add logic
                showToast("Error")
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        binding.tvForgotPassword.setOnClickListener {
            showToast("Soon")
        }
        binding.loginWithFacebook.setOnClickListener {
            showToast("Soon")
        }
        binding.loginWithGoogle.setOnClickListener {
            showToast("Soon")
        }
    }


    private fun isValidAccount(): Boolean {
       //todo add logic
        return true
    }

    private fun showToast(s:String){
        Toast.makeText(requireActivity(),s,Toast.LENGTH_SHORT).show()
    }

}