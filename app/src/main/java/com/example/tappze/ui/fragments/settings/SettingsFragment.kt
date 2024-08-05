package com.example.tappze.com.example.tappze.ui.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.ui.fragments.password_reset.ChangePasswordBottomSheet
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val authViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var appIntentUtil: AppIntentUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val selectedColor =
            ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSecondaryContainer)
        val unselectedColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        with(binding) {
            toggleGroup.addOnButtonCheckedListener { _, checkedId, _ ->
                listOf(btnProfileOff, btnProfileOn).forEach { button ->
                    button.setBackgroundColor(if (button.id == checkedId) selectedColor else unselectedColor)
                }
            }

            tvSubscription.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_subscriptionFragment)
            }

            tvChangePassword.setOnClickListener {
                ChangePasswordBottomSheet().show(
                    childFragmentManager,
                    ChangePasswordBottomSheet::class.java.simpleName
                )
            }

            tvSignOut.setOnClickListener {
                showLogOutDialog()
            }

            tvContactUs.setOnClickListener {
                observeContactUsIntent()
            }
            tvPurchase.setOnClickListener {
                val intent = appIntentUtil.buyTappzeCard()
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeContactUsIntent() {
        authViewModel.gmailIntent(requireContext())
        authViewModel.navigateToProfile.observe(viewLifecycleOwner) { intent ->
            intent?.let { startActivity(it) }
        }
        authViewModel.showAlertDialog.observe(viewLifecycleOwner) { show ->
            if (show) showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val message = "Gmail not found in your device!"
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_info)
            .setTitle("App Not Found")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->

            }
            .setNegativeButton("No", null)
            .show()
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

    private fun userLogOut() {
        val auth = FirebaseAuth.getInstance()
        preferencesHelper.deleteUser()
        auth.signOut()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://sign_in"))
        startActivity(intent)
    }

}
