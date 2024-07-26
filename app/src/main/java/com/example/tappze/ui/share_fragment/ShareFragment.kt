package com.example.tappze.com.example.tappze.ui.share_fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.tappze.databinding.FragmentShareBinding



class ShareFragment : Fragment() {

    private lateinit var binding: FragmentShareBinding
    private val url:String="https://github.com/Muneeb912-cmd/Tappze-Clone-Android"
    private lateinit var clipboardManager: ClipboardManager
    private val authViewModel: ShareFragmentViewModel by viewModels()


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
}