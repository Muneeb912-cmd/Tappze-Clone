package com.example.tappze.com.example.tappze.ui.fragments.share_fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentShareBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ShareFragment : Fragment() {

    private lateinit var binding: FragmentShareBinding
    private val url:String="https://github.com/Muneeb912-cmd/Tappze-Clone-Android"
    private lateinit var clipboardManager: ClipboardManager
    private val authViewModel: ShareFragmentViewModel by viewModels()
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateImgFromPref()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentShareBinding.inflate(inflater,container,false)
        clipboardManager = requireContext().getSystemService(ClipboardManager::class.java) as ClipboardManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateQrImgView()
        binding.copyUrl.setOnClickListener {
            copyUrlToClipboard()
        }
        binding.shareUrlBtn.setOnClickListener{
            authViewModel.shareUserIntent(requireContext(),url)
        }
    }

    private fun copyUrlToClipboard() {
        val clipData = ClipData.newPlainText("text", url)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this.context, "Url Copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun populateQrImgView(){
        val bitmap = authViewModel.generateQR(url)
        binding.qrCode.setImageBitmap(bitmap)
    }
    private fun populateImgFromPref() {
        val user = preferencesHelper.getUser()
        val userPhoto=user?.photo.toString()
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(userPhoto)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(binding.shareFragmentImg.context)
                .load(uri.toString())
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                        .skipMemoryCache(false) // Set to true if you want to skip memory cache
                )
                .into(binding.shareFragmentImg)
        }.addOnFailureListener {
            binding.shareFragmentImg.setImageResource(R.drawable.ic_photo)
        }
    }
}