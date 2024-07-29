package com.example.tappze.com.example.tappze.ui.add_edit_link

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

        arguments?.let {
            socialLink = it.getParcelable<SocialLinks>(ARG_DATA_KEY)!!
            link = it.getString("ARG_LINK_KEY")!!
        }
        userData = preferencesHelper.getUser()!!
        populateUi()
        binding.closeBottomSheet.setOnClickListener {
            dismiss()
        }

        binding.helpBtn.setOnClickListener{
            instructionAlertBox()
        }

        binding.deleteLink.setOnClickListener{
            deleteLink()
        }

        return binding.root
    }

    private fun populateUi() {
        socialLink.let {
            val linkItem = socialLinks.find { it.text == socialLink.text }
            linkItem?.let { item ->
                binding.socialLinkImgBottomSheet.setImageResource(item.imageResId)
                binding.bottomSheetHeading.text = item.text
                binding.socialLinkInput.hint = item.text
                helpText = helpTexts[item.text] ?: "No information available."
            }
        }
        binding.socialLinkInput.setText(link)
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createAccountBtn = binding.root.findViewById<Button>(R.id.button)
        val loadingIndicator = binding.root.findViewById<ProgressBar>(R.id.loadingIndicator)
        "Save".also { createAccountBtn.text = it }
        createAccountBtn.setOnClickListener {
            saveLink(createAccountBtn, loadingIndicator)
        }
    }

    private fun saveLink(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        authViewModel.addLink(
            userData.userId!!,
            binding.bottomSheetHeading.text.toString(),
            binding.socialLinkInput.text.toString()
        )
        observeState(createAccountBtn, loadingIndicator)
    }

    private fun observeState(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.saveLinksState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        ButtonStateUtils("Save").showLoadingIndicator(createAccountBtn, loadingIndicator)
                    }

                    is Response.Success -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(createAccountBtn, loadingIndicator)
                        dismiss()
                    }

                    is Response.Error -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(createAccountBtn, loadingIndicator)
                        Toast.makeText(
                            requireContext(),
                            "${response.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun instructionAlertBox() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(helpText)
        builder.setTitle("Instructions")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    companion object {
        private const val ARG_DATA_KEY = "argument_data_key"
        fun newInstance(socialLink: SocialLinks, link: String): SocialLinkBottomSheetFragment {
            val fragment = SocialLinkBottomSheetFragment()
            val args = Bundle().apply {
                putParcelable(ARG_DATA_KEY, socialLink)
                putString("ARG_LINK_KEY", link)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private fun deleteLink() {
        authViewModel.deleteLink(socialLink.text)

        dismiss()
    }
}


