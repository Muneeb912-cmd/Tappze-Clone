package com.example.tappze.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userName: String
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserDataFromPreferences()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.findViewById<ImageButton>(R.id.editProfile).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfile)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayUserName = view.findViewById<TextView>(R.id.displayUserName)
        displayUserName.text = userName
    }

    override fun onResume() {
        super.onResume()
        getUserDataFromPreferences()
    }

    private fun getUserDataFromPreferences() {
        val user=preferencesHelper.getUser()
        userId = user?.userId.toString()
        userName = user?.userName.toString()
    }


}
