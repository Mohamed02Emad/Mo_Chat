package com.mo_chatting.chatapp.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mo_chatting.chatapp.MainActivity
import com.mo_chatting.chatapp.appClasses.firebase_client_id
import com.mo_chatting.chatapp.databinding.FragmentLoginBinding
import com.mo_chatting.chatapp.validation.isValidEmail
import com.mo_chatting.chatapp.validation.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setOnClicks()
        setAfterTextChanges()
    }

    private fun setAfterTextChanges() {
        binding.etEmail.doAfterTextChanged {
            viewModel.setEmail(email = it.toString())
        }
        binding.etPassword.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }
    }

    private fun setViews() {
        binding.etEmail.setText(viewModel.email.value)
        binding.etPassword.setText(viewModel.password.value)
    }

    private fun setOnClicks() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            validateAccount(email, password)
        }
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        binding.tvForgotPassword.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                resetPassword(binding.etEmail.text.toString())
            }

            }


        binding.loginWithFacebook.setOnClickListener {
            showToast("Soon")
        }

        binding.loginWithGoogle.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //git ignored
                .requestIdToken(firebase_client_id)
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(requireActivity(), options)
            signInClient.signInIntent.also {
                resultLauncher.launch(it)
            }

        }
    }


    private fun validateAccount(email: String, password: String): Boolean {
        val emailValidationResult = isValidEmail(email)
        val passwordValidationResult = validatePassword(password)

        if (!emailValidationResult.isValid) {
            showToast(emailValidationResult.message)
            return false
        }
        if (!passwordValidationResult.isValid) {
            showToast(passwordValidationResult.message)
            return false
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                showToast(it.exception!!.message.toString())
            }
        }

        return true
    }

    private fun showToast(s: String) {
        Toast.makeText(requireActivity(), s, Toast.LENGTH_LONG).show()
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val data = result.data
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
            } catch (_: Exception) {
            }
        }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signInWithCredential(credentials).await()
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private suspend fun resetPassword(email: String?) {
        if (email.isNullOrBlank()){
            withContext(Dispatchers.Main) {
                showToast("Enter your Email first")
            }
                return

        }
        firebaseAuth.sendPasswordResetEmail(email).await()
        withContext(Dispatchers.Main) {
            showToast("check your email")
        }
    }

}