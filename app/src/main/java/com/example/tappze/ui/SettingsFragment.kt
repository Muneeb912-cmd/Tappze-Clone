package com.example.tappze.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.ui.password_reset.ChangePasswordBottomSheet
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toggleGroup = binding.toggleGroup
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSecondaryContainer)
        val unselectedColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            val buttons = listOf(
                binding.btnProfileOff,
                binding.btnProfileOn
            )

            buttons.forEach { button ->
                if (button.id == checkedId) {
                    button.setBackgroundColor(selectedColor)
                } else {
                    button.setBackgroundColor(unselectedColor)
                }
            }
        }

        binding.tvSubscription.setOnClickListener{
            findNavController().navigate(R.id.action_settingsFragment_to_subscriptionFragment)
        }

        binding.tvChangePassword.setOnClickListener{
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.tvSignOut.setOnClickListener{
            showLogOutDialog()
        }
    }

    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to LogOut ?")
        builder.setTitle("Logout ?")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirm") { _, _ ->
            userLogOut()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun userLogOut(){
        val auth = FirebaseAuth.getInstance()
        preferencesHelper.deleteUser()
        auth.signOut()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://sign_in"))
        startActivity(intent)
    }
}
