package com.mo_chatting.chatapp.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.databinding.FragmentLoginBinding
import com.mo_chatting.chatapp.validation.isValidEmail
import com.mo_chatting.chatapp.validation.validatePassword

class LoginFragment : Fragment() {

    lateinit var binding:FragmentLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth=FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            validateAccount(email,password)
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


    private fun validateAccount(email:String,password:String): Boolean {
        val emailValidationResault = isValidEmail(email)
        val passwordValidationResault = validatePassword(password)

        Log.d("mohamed", "validateAccount: email $email \n isVlaid ${emailValidationResault.isValid} /n messeage ${emailValidationResault.message}")
        if (!emailValidationResault.isValid){
            showToast(emailValidationResault.message)
            return false
        }
        if (!passwordValidationResault.isValid){
            showToast(passwordValidationResault.message)
            return false
        }

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(requireActivity(),MainActivity::class.java))
                requireActivity().finish()
            }else{
                showToast("no user")
            }
        }

        return true
    }

    private fun showToast(s:String){
        Toast.makeText(requireActivity(),s,Toast.LENGTH_SHORT).show()
    }

}