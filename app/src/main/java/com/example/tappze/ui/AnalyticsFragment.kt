package com.example.tappze.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentAnalyticsBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateImgFromPref()
    }

    private fun populateImgFromPref() {
        val user = preferencesHelper.getUser()
        val userPhoto = user?.photo.toString()
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(userPhoto)

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(binding.analyticsFragImg.context)
                .load(uri.toString())
                .thumbnail(0.1f) // Optional: use thumbnail to improve perceived performance
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_photo) // Placeholder image
                        .error(R.drawable.ic_photo) // Error image
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                        .skipMemoryCache(false) // Use memory cache
                )
                .into(binding.analyticsFragImg)
        }.addOnFailureListener {
            binding.analyticsFragImg.setImageResource(R.drawable.ic_photo) // Fallback image
        }
    }
}
