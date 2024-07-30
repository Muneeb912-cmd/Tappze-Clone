package com.example.tappze.com.example.tappze.ui.add_edit_link

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentSocialLinkBottomSheetBinding
import com.example.tappze.com.example.tappze.models.SocialLinks
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.utils.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SocialLinkBottomSheetFragment : BottomSheetDialogFragment() {

    private val authViewModel: AddEditLinkViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: FragmentSocialLinkBottomSheetBinding
    private lateinit var userData: User
    private lateinit var socialLink: SocialLinks
    private lateinit var link: String
    private lateinit var helpText: String

    @Inject
    lateinit var helpTexts: Map<String, String>

    @Inject
    lateinit var socialLinks: ArrayList<SocialLinks>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSocialLinkBottomSheetBinding.inflate(inflater, container, false)
        setupUi()
        return binding.root
    }

    private fun setupUi() {
        arguments?.let {
            socialLink = it.getParcelable(ARG_DATA_KEY)!!
            link = it.getString(ARG_LINK_KEY)!!
        }
        userData = preferencesHelper.getUser()!!
        populateUi()
        binding.apply {
            createAccountBtn.button.text="Save"
            closeBottomSheet.setOnClickListener { dismiss() }
            helpBtn.setOnClickListener { showInstructionAlertBox() }
            deleteLink.setOnClickListener { deleteLink() }
            createAccountBtn.button.setOnClickListener { saveLink(createAccountBtn.button, createAccountBtn.loadingIndicator) }
            openLink.setOnClickListener { openLink() }
        }
    }

    private fun populateUi() {
        socialLinks.find { it.text == socialLink.text }?.let { item ->
            binding.apply {
                socialLinkImgBottomSheet.setImageResource(item.imageResId)
                bottomSheetHeading.text = item.text
                socialLinkInput.hint = item.text
            }
            helpText = helpTexts[item.text] ?: "No information available."
            binding.socialLinkInput.setText(link)
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun saveLink(button: Button, loadingIndicator: ProgressBar) {
        authViewModel.addLink(
            userData.userId!!,
            binding.bottomSheetHeading.text.toString(),
            binding.socialLinkInput.text.toString()
        )
        observeState(button, loadingIndicator)
    }

    private fun observeState(button: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.saveLinksState.collect { response ->
                when (response) {
                    is Response.Loading -> ButtonStateUtils("Save").showLoadingIndicator(button, loadingIndicator)
                    is Response.Success -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(button, loadingIndicator)
                        dismiss()
                    }
                    is Response.Error -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(button, loadingIndicator)
                        Toast.makeText(requireContext(), response.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showInstructionAlertBox() {
        AlertDialog.Builder(requireContext())
            .setTitle("Instructions")
            .setMessage(helpText)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteLink() {
        authViewModel.deleteLink(socialLink.text)
        dismiss()
    }

    private fun openLink() {
        authViewModel.openProfile(socialLink.text, link, requireContext())
        authViewModel.navigateToProfile.observe(viewLifecycleOwner) { intent ->
            intent?.let { startActivity(it) }
        }
        authViewModel.showAlertDialog.observe(viewLifecycleOwner) { show ->
            if (show) showAlertDialog(socialLink.text, link)
        }
    }

    private fun showAlertDialog(appName: String, link: String) {
        val message = "The $appName app is not installed. Do you want to proceed to the website?"
        val websiteUrl = when (appName) {
            "Instagram" -> "https://www.instagram.com/$link"
            "Facebook" -> "https://www.facebook.com/$link"
            "TikTok" -> "https://www.tiktok.com/@$link"
            "WhatsApp" -> "https://wa.me/$link"
            "LinkedIn" -> "https://www.linkedin.com/in/$link"
            "Telegram" -> "https://t.me/$link"
            "Snapchat" -> "https://www.snapchat.com/add/$link"
            "X" -> "https://x.com/$link"
            "YouTube" -> "https://www.youtube.com/channel/$link"
            "Spotify" -> "https://open.spotify.com/playlist/$link"
            "PayPal" -> "https://www.paypal.com/$link"
            "Pinterest" -> "https://www.pinterest.com/$link"
            "Skype" -> "https://join.skype.com/$link"
            else -> "https://www.example.com"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("App Not Found")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }


    companion object {
        private const val ARG_DATA_KEY = "argument_data_key"
        private const val ARG_LINK_KEY = "ARG_LINK_KEY"

        fun newInstance(socialLink: SocialLinks, link: String): SocialLinkBottomSheetFragment {
            return SocialLinkBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA_KEY, socialLink)
                    putString(ARG_LINK_KEY, link)
                }
            }
        }
    }
}