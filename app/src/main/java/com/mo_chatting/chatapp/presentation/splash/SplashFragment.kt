package com.mo_chatting.chatapp.presentation.splash

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mo_chatting.chatapp.AuthActivity
import com.mo_chatting.chatapp.databinding.FragmentSplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setLottieAnimation()
        lifecycleScope.launch(Dispatchers.Main) {
        delay(300)
            try {
                requireActivity().startActivity(
                    Intent(
                        requireContext(),
                        AuthActivity::class.java
                    )
                )
                requireActivity().finish()
            }catch (_:Exception){
            }
        }
        }

//    private fun setLottieAnimation() {
//        binding.splashAnimation.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(p0: Animator) {}
//            override fun onAnimationCancel(p0: Animator) {}
//            override fun onAnimationRepeat(p0: Animator) {}
//            override fun onAnimationEnd(p0: Animator) {
//                lifecycleScope.launch(Dispatchers.Main) {
//                    try {
//                        requireActivity().startActivity(
//                            Intent(
//                                requireContext(),
//                                AuthActivity::class.java
//                            )
//                        )
//                        requireActivity().finish()
//                    }catch (_:Exception){
//                    }
//                }
//            }
//        }
//        )
//    }
}