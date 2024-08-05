package com.example.tappze.com.example.tappze.ui.fragments.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import com.example.tappze.databinding.FragmentNfcBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NfcFragment : Fragment() {

    private lateinit var binding: FragmentNfcBinding
    private val viewModel: NfcViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
    @Inject
    lateinit var appIntentUtil: AppIntentUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNfcBinding.inflate(inflater, container, false)
        setupObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buyTagButton.setOnClickListener{
            binding.buyTagButton.setOnClickListener {
                val intent = appIntentUtil.buyTappzeCard()
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }
    }

    private fun showLoading() {
        binding.loadingOverlay.loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingOverlay.loadingIndicator.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "NFC not supported on this device!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(requireContext(), "Please enable NFC!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireContext(), javaClass)
            .addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
        pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        nfcAdapter!!.enableForegroundDispatch(requireActivity(), pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }
    private fun readNfcTag(tag: Tag) {
        viewModel.setLoading(true)
        try {
            val ndef = Ndef.get(tag)
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            val payload = ndefMessage.records[0].payload
            val text = String(payload, charset("UTF-8"))
            // Handle the NFC tag content
            Toast.makeText(requireContext(), "Tag content: $text", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            viewModel.setLoading(false)
        }
    }
}
