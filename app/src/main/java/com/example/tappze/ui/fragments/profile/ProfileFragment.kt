package com.example.tappze.com.example.tappze.ui.fragments.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tappze.R
import com.example.tappze.adapters.SocialLinkAdapter
import com.example.tappze.com.example.tappze.models.SocialLinks
import com.example.tappze.com.example.tappze.ui.fragments.add_edit_link.SocialLinkBottomSheetFragment
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentProfileBinding
import com.example.tappze.utils.Response
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), SocialLinkAdapter.OnItemClickListener {

    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userPhoto: String

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var imageResMap: Map<String, Int>

    private val authViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var socialLinkAdapter: SocialLinkAdapter
    private var dataList = ArrayList<SocialLinks>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserDataFromPreferences()

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfile)
        }
        binding.cardView.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfile)
        }
        initializeAdapter()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
        observeLinks()
    }

    private fun populateData() {
        binding.displayUserName.text = userName
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(userPhoto)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(binding.userPhoto.context)
                .load(uri.toString())
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                )
                .into(binding.userPhoto)
        }.addOnFailureListener {
            binding.userPhoto.setImageResource(R.drawable.ic_photo)
        }
    }

    private fun initializeAdapter() {
        socialLinkAdapter = SocialLinkAdapter(dataList, this)
        binding.savedLinks.layoutManager = GridLayoutManager(context, 3)
        binding.savedLinks.adapter = socialLinkAdapter
    }

    private fun observeLinks() {
        lifecycleScope.launch {
            authViewModel.linksState.collect { links ->
                val socialLinksList = links.toSocialLinksList(imageResMap)
                dataList.clear()
                dataList.addAll(socialLinksList)
                socialLinkAdapter.notifyDataSetChanged()
            }
        }
        observeState()
    }


    override fun onResume() {
        super.onResume()
        getUserDataFromPreferences()
    }

    private fun getUserDataFromPreferences() {
        val user = preferencesHelper.getUser()
        userId = user?.userId.toString()
        userName = user?.userName.toString()
        userPhoto = user?.photo.toString()
        authViewModel.getSavedLinks(userId)
        authViewModel.observeLinks(userId)
    }

    private fun observeState() {
        lifecycleScope.launch {
            authViewModel.saveLinksState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.savedLinks.visibility = View.GONE
                        binding.errorTextView.visibility = View.GONE
                        binding.refreshButton.visibility = View.GONE
                    }

                    is Response.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorTextView.visibility = View.GONE
                        binding.savedLinks.visibility = View.VISIBLE
                        binding.refreshButton.visibility = View.GONE

//                        if (response.data.isNullOrEmpty()) {
//                            binding.errorTextView.visibility = View.VISIBLE
//                            binding.errorTextView.text = "No Links Added"
//                            binding.refreshButton.visibility = View.VISIBLE
//                        } else {
//                            binding.errorTextView.visibility = View.GONE
//                        }
                    }

                    is Response.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.savedLinks.visibility = View.GONE
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.refreshButton.visibility = View.VISIBLE
                        binding.errorTextView.text = response.exception.message
                    }
                }
            }
        }
    }
    private fun Map<String, String>.toSocialLinksList(imageResMap: Map<String, Int>): List<SocialLinks> {
        return this.map { (key, _) ->
            val imageResId = imageResMap[key] ?: R.drawable.ic_photo
            SocialLinks(imageResId, key, isSaved = false)
        }
    }
    override fun onItemClick(socialLink: SocialLinks) {
        var link: String = ""
        authViewModel.links.observe(viewLifecycleOwner) { links ->
            val existingLink = links[socialLink.text]
            if (existingLink != null) {
                link = existingLink
            }
        }
        val bottomSheet = SocialLinkBottomSheetFragment.newInstance(socialLink, link)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }
}
