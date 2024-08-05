package com.example.tappze.com.example.tappze.ui.fragments.password_reset

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.databinding.FragmentChangePasswordBottomSheetBinding
import com.example.tappze.utils.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChangePasswordBottomSheetBinding
    private val authViewModel: ChangePasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBottomSheetBinding.inflate(inflater, container, false)
        val createAccountBtn = binding.root.findViewById<Button>(R.id.button)
        val loadingIndicator = binding.root.findViewById<ProgressBar>(R.id.loadingIndicator)
        "Send".also { createAccountBtn.text = it }
        createAccountBtn.setOnClickListener {
            sendPasswordResetEmail(createAccountBtn, loadingIndicator)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBottomSheet.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    private fun observeState(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.passwordResetResult.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        ButtonStateUtils("Send").showLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                    }

                    is Response.Success -> {
                        ButtonStateUtils("Send").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        onSuccess()
                    }

                    is Response.Error -> {
                        ButtonStateUtils("Send").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )

                    }
                }
            }
        }
    }

    private fun sendPasswordResetEmail(createAccountBtn:Button, loadingIndicator:ProgressBar){
        val email = binding.email.text.toString()
        authViewModel.sendPasswordResetEmail(email)
        observeState(createAccountBtn, loadingIndicator)
    }

    private fun onSuccess(){
        Toast.makeText(requireContext(),"Password Reset Email Sent!",Toast.LENGTH_SHORT).show()
        dismiss()
    }
}