package com.example.tappze.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    lateinit var createAccBtn:AppCompatButton
    lateinit var signInText:TextView
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = preferencesHelper.getUser()
        if (user != null) {
            findNavController().navigate(R.id.action_createAccountFragment_to_mainAppActivity2)
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_create_account_, container, false)
        createAccBtn=view.findViewById(R.id.createAccountBtn)
        signInText=view.findViewById(R.id.signInText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAccBtn.setOnClickListener{
            findNavController().navigate(R.id.action_createAccountFragment_to_signUpFragment)
        }

        signInText.setOnClickListener{
            findNavController().navigate(R.id.action_createAccountFragment_to_signInFragment)
        }

    }

}