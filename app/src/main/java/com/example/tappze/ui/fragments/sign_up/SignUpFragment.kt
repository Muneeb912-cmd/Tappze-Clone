package com.example.tappze.com.example.tappze.ui.fragments.sign_up

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.ui.shared_view_model.SharedViewModel
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.databinding.FragmentSignUpBinding
import com.example.tappze.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val authViewModel: SignUpViewModel by viewModels()

    @Inject
    lateinit var appIntentUtil: AppIntentUtil
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val privacyPolicyText = "By signing up you agree to our Privacy Policy and Terms of Use"
        createSpannableString(privacyPolicyText)
        binding.privacyPolicy.movementMethod = LinkMovementMethod.getInstance()

        binding.singUpToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createAccountBtn = binding.createAccountBtn.button

        createAccountBtn.setOnClickListener {
            val name = binding.fullName.text.toString()
            val userName = binding.userName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            handleSignUp(name, userName, email, password)
        }

        sharedViewModel.networkStatus.observe(viewLifecycleOwner, Observer { isConnected ->
            updateNetworkStatus(isConnected)
        })
    }

    private fun handleSignUp(name: String, userName: String, email: String, password: String) {
        val errors = authViewModel.validateFields(name, userName, email, password)

        if (errors.isEmpty()) {
            authViewModel.signUpWithEmailPassword(email, password, name, userName)
            observeState(binding.createAccountBtn.button, binding.createAccountBtn.loadingIndicator)
        } else {
            setFieldErrors(errors)
        }
    }

    private fun setFieldErrors(errors: Map<String, String?>) {
        binding.fullName.error = errors["name"]
        binding.userName.error = errors["userName"]
        binding.email.error = errors["email"]
        binding.password.error = errors["password"]
    }


    private fun updateNetworkStatus(isConnected: Boolean) {
        if (isConnected) {
            Toast.makeText(requireContext(), "Online", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeState(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.signUpState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        ButtonStateUtils("Sign Up").showLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                    }

                    is Response.Success -> {
                        ButtonStateUtils("Sign Up").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        findNavController().popBackStack()
                    }

                    is Response.Error -> {
                        ButtonStateUtils("Sign Up").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        binding.errorText.text = response.exception.message
                    }
                }
            }
        }
    }

    private fun createSpannableString(privacyPolicyText: String) {
        val spannableString = SpannableString(privacyPolicyText)
        val clickableTextColor =
            ContextCompat.getColor(requireContext(), R.color.md_theme_dark_surface)

        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = appIntentUtil.tappzePrivacyPolicy()
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = clickableTextColor
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.textSize = 50.toFloat()
            }
        }
        val privacyPolicyStart = privacyPolicyText.indexOf("Privacy Policy")
        val privacyPolicyEnd = privacyPolicyStart + "Privacy Policy".length
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            privacyPolicyStart,
            privacyPolicyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val termsOfUseClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = appIntentUtil.tappzeTermsAndServices()
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = clickableTextColor
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.textSize = 50.toFloat()
            }
        }
        val termsOfUseStart = privacyPolicyText.indexOf("Terms of Use")
        val termsOfUseEnd = termsOfUseStart + "Terms of Use".length
        spannableString.setSpan(
            termsOfUseClickableSpan,
            termsOfUseStart,
            termsOfUseEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.privacyPolicy.text = spannableString
    }
}
