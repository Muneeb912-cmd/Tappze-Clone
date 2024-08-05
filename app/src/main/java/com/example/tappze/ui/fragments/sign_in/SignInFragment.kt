package com.example.tappze.com.example.tappze.ui.fragments.sign_in

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentSignInBinding
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.com.example.tappze.ui.fragments.password_reset.ChangePasswordBottomSheet
import com.example.tappze.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var signUpText: TextView
    private lateinit var welcomeTextView: TextView
    private lateinit var binding: FragmentSignInBinding
    private val authViewModel: SignInViewModel by viewModels()
    private lateinit var userData: User
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        signUpText = binding.signUpText
        welcomeTextView = binding.welcomeText

        val toolbar = binding.singInToolbar
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createAccountBtn = binding.root.findViewById<Button>(R.id.button)
        val loadingIndicator = binding.root.findViewById<ProgressBar>(R.id.loadingIndicator)
        "Sign In".also { createAccountBtn.text = it }
        createSpan()
        createAccountBtn.setOnClickListener {
            validateAndLogin(createAccountBtn, loadingIndicator)
        }
        binding.forgetPassword.setOnClickListener {
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }


    private fun validateAndLogin(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        authViewModel.signInWithEmailPassword(email, password)
        observeState(createAccountBtn, loadingIndicator)
    }

    private fun observeState(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.signInState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        ButtonStateUtils("Sign In").showLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                    }

                    is Response.Success -> {
                        ButtonStateUtils("Sign In").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        onSuccess()
                    }

                    is Response.Error -> {
                        ButtonStateUtils("Sign In").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        binding.errorText.text = response.exception.message
                    }
                }
            }
        }
    }

    private fun onSuccess() {
        authViewModel.userData.observe(viewLifecycleOwner) { user ->
            userData = user
            saveUserDataToPreferences(user) // Save user data to SharedPreferences
            findNavController().navigate(R.id.action_signInFragment_to_mainAppActivity2)
        }
    }

    private fun createSpan() {
        val ss = SpannableString("Don't have an account? Sign Up")
        val welcomeText = SpannableString("Welcome back,\nSign In your account")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.BLACK)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val boldSpan = android.text.style.StyleSpan(Typeface.BOLD)
        ss.setSpan(boldSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        welcomeText.setSpan(boldSpan, 26, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        signUpText.text = ss
        welcomeTextView.text = welcomeText
        signUpText.movementMethod = LinkMovementMethod.getInstance()
        signUpText.highlightColor = Color.TRANSPARENT
    }

    private fun saveUserDataToPreferences(user: User) {
        preferencesHelper.saveUser(user)
    }


}