package com.mo_chatting.chatapp.presentation.signUp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mo_chatting.chatapp.presentation.MainActivity.MainActivity
import com.mo_chatting.chatapp.R
import com.mo_chatting.chatapp.appClasses.isValidEmail
import com.mo_chatting.chatapp.appClasses.validatePassword
import com.mo_chatting.chatapp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
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
        binding.btnSignUp.apply {
            setOnClickListener {
                startAnimation {
                    binding.progressBar.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        accountCreated()
                        binding.progressBar.visibility = View.INVISIBLE
                        revertAnimation()
                    }
                }
            }
        }
    }

    private suspend fun accountCreated() {


        val emailValidationResault = isValidEmail(viewModel.email)
        val passwordValidationResault = validatePassword(viewModel.passwrod)

        if (!emailValidationResault.isValid) {
            showToast(emailValidationResault.message)
            return
        }
        if (!passwordValidationResault.isValid) {
            showToast(passwordValidationResault.message)
            return
        }

        if (viewModel.passwrod != viewModel.confirmPassword) {
            showToast("password don't match")
            return
        }

        try {
            firebaseAuth.createUserWithEmailAndPassword(viewModel.email, viewModel.passwrod).await()
                firebaseAuth.currentUser?.let { user ->
                    val name = binding.etUserName.text.toString()
                    val imageUri =
                        Uri.parse("android.resource://${requireActivity().packageName}/${R.drawable.ic_profile}")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(imageUri)
                        .build()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            user.updateProfile(profileUpdates).await()
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        } catch (_: Exception) {
                        }
                    }

                }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun showToast(s: String) =
        Toast.makeText(requireActivity(), s, Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        super.onDestroy()
        binding.btnSignUp.dispose()
    }

}