package com.mo_chatting.chatapp.presentation.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.databinding.FragmentSignUpBinding
import com.mo_chatting.chatapp.validation.isValidEmail
import com.mo_chatting.chatapp.validation.validatePassword

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setOnClicks()
        setAfterChangeListeners()
    }

    private fun setAfterChangeListeners() {
        binding.apply {
            etUserName.doAfterTextChanged { viewModel.userName = it.toString() }
            etEmail.doAfterTextChanged { viewModel.email = it.toString() }
            etPassword.doAfterTextChanged { viewModel.passwrod = it.toString() }
            etConfirmPassword.doAfterTextChanged { viewModel.confirmPassword = it.toString() }
        }
    }

    private fun setViews() {
        binding.apply {
            etUserName.setText(viewModel.userName)
            etEmail.setText(viewModel.email)
            etPassword.setText(viewModel.passwrod)
            etConfirmPassword.setText(viewModel.confirmPassword)
        }
    }

    private fun setOnClicks() {
        binding.btnBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignUp.setOnClickListener {
            accountCreated()
        }
    }

    private fun accountCreated(): Boolean {


        val emailValidationResault = isValidEmail(viewModel.email)
        val passwordValidationResault = validatePassword(viewModel.passwrod)

        if (!emailValidationResault.isValid) {
            showToast(emailValidationResault.message)
            return false
        }
        if (!passwordValidationResault.isValid) {
            showToast(passwordValidationResault.message)
            return false
        }

        if (viewModel.passwrod != viewModel.confirmPassword) {
            showToast("password don't match")
            return false
        }

        firebaseAuth.createUserWithEmailAndPassword(viewModel.email, viewModel.passwrod)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    showToast(it.exception!!.message.toString())
                }
            }
        return true
    }

    private fun showToast(s: String) =
        Toast.makeText(requireActivity(), s, Toast.LENGTH_LONG).show()


}