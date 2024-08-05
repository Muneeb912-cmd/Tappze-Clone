package com.example.tappze.com.example.tappze.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tappze.R
import com.example.tappze.com.example.tappze.ui.shared_view_model.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainAppActivityFragment : Fragment() {
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_app_activity, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nested_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomNavigation(view)
        sharedViewModel.networkStatus.observe(viewLifecycleOwner, Observer { isConnected ->
            updateNetworkStatus(isConnected)
        })

    }

    private fun setUpBottomNavigation(view: View) {
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavView)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        if (isConnected) {
            Toast.makeText(requireContext(), "User Online", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "User Offline", Toast.LENGTH_SHORT).show()
        }
    }

}
