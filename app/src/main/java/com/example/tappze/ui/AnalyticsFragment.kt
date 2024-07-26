package com.example.tappze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tappze.R
import com.example.tappze.databinding.FragmentAnalyticsBinding


class AnalyticsFragment : Fragment() {

    lateinit var binding:FragmentAnalyticsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAnalyticsBinding.inflate(inflater,container,false)
        return binding.root
    }

}