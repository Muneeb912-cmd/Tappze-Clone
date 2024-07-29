package com.example.tappze.com.example.tappze.ui.edit_profile

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tappze.R
import com.example.tappze.adapters.CustomAlertAdapter
import com.example.tappze.adapters.SocialLinkAdapter
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.databinding.FragmentEditProfileBinding
import com.example.tappze.com.example.tappze.models.SocialLinks
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.com.example.tappze.ui.add_edit_link.SocialLinkBottomSheetFragment
import com.example.tappze.com.example.tappze.utils.ButtonStateUtils
import com.example.tappze.com.example.tappze.utils.PermissionUtils
import com.example.tappze.utils.Response
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var userData: User
    private val authViewModel: EditProfileViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private val REQUEST_CODE_PERMISSIONS = 1001
    private lateinit var photoUri: Uri

    @Inject
    lateinit var socialLinks: ArrayList<SocialLinks>

    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.addImage.setImageURI(photoUri)
                binding.addImage.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                Log.d("EditProfileFragment", "Image capture failed.")
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
        checkPermissions()


        val createAccountBtn = binding.root.findViewById<Button>(R.id.button)
        val loadingIndicator = binding.root.findViewById<ProgressBar>(R.id.loadingIndicator)
        createAccountBtn.text = "Save"

        binding.addImage.setOnClickListener {
            showImageSourceDialog()
        }

        setupSocialLinkAdapter()

        getUserDataFromPreferences()

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        createAccountBtn.setOnClickListener {
            onSaveClick(createAccountBtn, loadingIndicator)
        }
        binding.selectDob.setOnClickListener {
            selectDate()
        }
    }

    private fun setupSocialLinkAdapter() {
        val recyclerView: RecyclerView = binding.socialLinks
        recyclerView.layoutManager = GridLayoutManager(context, 3) // 3 columns
        recyclerView.adapter = SocialLinkAdapter(socialLinks, this)
    }

    private fun populateGenderDropDown() {
        val autoCompleteTextView = binding.selectGender
        val items = resources.getStringArray(R.array.gender)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, R.id.dropdown_item, items)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun selectDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Specify your custom theme resource ID
        val themeResId = R.style.CustomDatePickerDialogTheme

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            themeResId, // Apply the custom theme
            { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth-${monthOfYear + 1}-$year"
                binding.selectDob.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.handlePermissionsResult(
            requestCode,
            permissions,
            grantResults,
            {
                Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(requireContext(), "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @SuppressLint("InflateParams")
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        val icons = arrayOf(
            R.drawable.ic_camera,
            R.drawable.ic_photo
        ) // Replace with your actual drawable resource IDs

        val adapter = CustomAlertAdapter(requireContext(), options, icons)

        AlertDialog.Builder(requireContext())
            .setTitle("Choose")
            .setAdapter(adapter) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val photoFile: File? = createImageFile()
        photoFile?.let {
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
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            var currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickPhotoIntent)
    }

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageView: ImageView = binding.addImage
                val selectedImageUri = result.data?.data
                imageView.setImageURI(selectedImageUri)
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

    private fun getUserDataFromPreferences() {
        userData = preferencesHelper.getUser()!!
        authViewModel.getSavedLinks(userData.userId!!)
        populateData()
    }

    private fun populateData() {
        binding.editUserName.setText(userData.userName ?: "")
        binding.editAboutYourself.setText(userData.aboutYourself ?: "")
        binding.editPhone.setText(userData.phone ?: "")
        binding.editCompany.setText(userData.company ?: "")
        binding.selectDob.setText(userData.dob ?: "")

        val genderAdapter = binding.selectGender.adapter as ArrayAdapter<String>
        val genderPosition = genderAdapter.getPosition(userData.gender)
        binding.selectGender.setText(genderAdapter.getItem(genderPosition), false)

        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(userData.photo!!)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            photoUri = uri
            Glide.with(binding.addImage.context)
                .load(uri.toString())
                .into(binding.addImage)
        }.addOnFailureListener {
            binding.addImage.setImageResource(R.drawable.ic_photo) // Set a placeholder image or handle error
        }
    }

    private fun onSaveClick(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        val updatedUser = User(
            userId = userData.userId,
            userName = binding.editUserName.text.toString(),
            dob = binding.selectDob.text.toString(),
            photo = photoUri.toString(),
            gender = binding.selectGender.text.toString(),
            phone = binding.editPhone.text.toString(),
            company = binding.editCompany.text.toString(),
            email = userData.email,
            aboutYourself = binding.editAboutYourself.text.toString(),
            fullName = userData.fullName
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
                        onSuccess()
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

    private fun onSuccess() {
        findNavController().popBackStack()
    }

    private fun checkPermissions() {
        PermissionUtils.checkAndRequestPermissions(
            requireActivity(),
            REQUEST_CODE_PERMISSIONS,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}
