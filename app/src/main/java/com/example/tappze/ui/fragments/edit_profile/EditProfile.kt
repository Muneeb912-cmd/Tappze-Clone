package com.example.tappze.com.example.tappze.ui.fragments.edit_profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.tappze.R
import com.example.tappze.adapters.CustomAlertAdapter
import com.example.tappze.adapters.SocialLinkAdapter
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentEditProfileBinding
import com.example.tappze.com.example.tappze.models.SocialLinks
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.com.example.tappze.ui.fragments.add_edit_link.SocialLinkBottomSheetFragment
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.com.example.tappze.utils.PermissionUtils
import com.example.tappze.utils.Response
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EditProfile : Fragment(), SocialLinkAdapter.OnItemClickListener {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var socialLinks: ArrayList<SocialLinks>
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var userData: User
    private val authViewModel: EditProfileViewModel by viewModels()
    private val REQUEST_CODE_PERMISSIONS = 1001
    private lateinit var photoUri: Uri
    private val DATE_FORMAT = "yyyyMMdd"
    private val IMAGE_PREFIX = "JPEG_"
    private val IMAGE_SUFFIX = ".jpg"
    private lateinit var dataList: Map<String, String>
    private var currentPhotoPath: String? = null


    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.addImage.setImageURI(photoUri)
                binding.addImage.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                Log.d("EditProfileFragment", "Image capture failed.")
            }
        }

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.addImage.setImageURI(result.data?.data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        populateGenderDropDown()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        getUserDataFromPreferences()
        observeLinks()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        with(binding) {
            selectGender.setAdapter(createAdapter(R.array.gender))
            addImage.setOnClickListener { showImageSourceDialog() }
            setupSocialLinkAdapter()
            selectDob.setOnClickListener { showDatePicker() }
            cancelBtn.setOnClickListener { findNavController().popBackStack() }
            createAccountBtn.button.text="Save"
            createAccountBtn.button.setOnClickListener {
                onSaveClick(
                    createAccountBtn.button,
                    createAccountBtn.loadingIndicator
                )
            }
        }
        checkAndRequestPermissions()
    }

    private fun createAdapter(arrayResId: Int) = ArrayAdapter(
        requireContext(),
        R.layout.dropdown_item,
        R.id.dropdown_item,
        resources.getStringArray(arrayResId)
    )

    @SuppressLint("SetTextI18n")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialogTheme,
            { _, year, month, day ->
                binding.selectDob.setText("$day-${month + 1}-$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupSocialLinkAdapter() {
        binding.socialLinks.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = SocialLinkAdapter(socialLinks, this@EditProfile)
        }
    }

    private fun populateGenderDropDown() {
        binding.selectGender.setAdapter(createAdapter(R.array.gender))
    }

    private fun showImageSourceDialog() {
        showAlertDialog(
            title = "Choose",
            options = arrayOf("Camera", "Gallery"),
            icons = arrayOf(R.drawable.ic_camera, R.drawable.ic_photo)
        ) { which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
    }

    private fun showAlertDialog(
        title: String,
        options: Array<String>,
        icons: Array<Int>,
        onItemClick: (Int) -> Unit
    ) {
        val adapter = CustomAlertAdapter(requireContext(), options, icons)
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setAdapter(adapter) { _, which -> onItemClick(which) }
            .show()
    }


    private fun openCamera() {
        createImageFile()?.let {
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.tappze.provider",
                it
            )
            takePictureLauncher.launch(photoUri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "$IMAGE_PREFIX$timeStamp",
            IMAGE_SUFFIX,
            storageDir
        ).apply { currentPhotoPath = absolutePath }
    }

    private fun openGallery() {
        galleryLauncher.launch(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )
    }

    private fun observeLinks() {
        lifecycleScope.launch {
            authViewModel.linksState.collect { links ->
                dataList = links
            }
        }
    }

    override fun onItemClick(socialLink: SocialLinks) {
        val link = dataList[socialLink.text] ?: ""
        SocialLinkBottomSheetFragment.newInstance(socialLink, link)
            .show(childFragmentManager, null)
    }

    private fun getUserDataFromPreferences() {
        userData = preferencesHelper.getUser()!!
        authViewModel.observeLinks(userId = userData.userId!!)
        populateData()
    }

    private fun populateData() {
        with(binding) {
            editUserName.setText(userData.userName ?: "")
            editAboutYourself.setText(userData.aboutYourself ?: "")
            editPhone.setText(userData.phone ?: "")
            editCompany.setText(userData.company ?: "")
            selectDob.setText(userData.dob ?: "")

            val genderAdapter = binding.selectGender.adapter as ArrayAdapter<String>
            val genderPosition = genderAdapter.getPosition(userData.gender)
            binding.selectGender.setText(genderAdapter.getItem(genderPosition), false)

            FirebaseStorage.getInstance().getReferenceFromUrl(userData.photo!!)
                .downloadUrl.addOnSuccessListener { uri ->
                    photoUri = uri
                    Glide.with(addImage.context)
                        .load(uri.toString())
                        .into(addImage)
                }.addOnFailureListener {
                    addImage.setImageResource(R.drawable.ic_photo)
                }
        }
    }

    private fun onSaveClick(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        val updatedUser = userData.copy(
            userName = binding.editUserName.text.toString(),
            dob = binding.selectDob.text.toString(),
            photo = photoUri.toString(),
            gender = binding.selectGender.text.toString(),
            phone = binding.editPhone.text.toString(),
            company = binding.editCompany.text.toString(),
            aboutYourself = binding.editAboutYourself.text.toString()
        )

        authViewModel.uploadImgAndUpdateUser(updatedUser)
        observeState(createAccountBtn, loadingIndicator)
    }

    private fun observeState(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        lifecycleScope.launch {
            authViewModel.updateUserState.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        ButtonStateUtils("Save").showLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                    }

                    is Response.Success -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
                        findNavController().popBackStack()
                    }

                    is Response.Error -> {
                        ButtonStateUtils("Save").hideLoadingIndicator(
                            createAccountBtn,
                            loadingIndicator
                        )
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


    private fun checkAndRequestPermissions() {
        PermissionUtils.checkAndRequestPermissions(
            requireActivity(),
            REQUEST_CODE_PERMISSIONS,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    }
}

