package com.example.tappze.com.example.tappze.repository

import android.graphics.Bitmap
import androidx.core.net.toUri
import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepoImplementation @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val preferencesHelper: PreferencesHelper,
    private val firebaseStorage: FirebaseStorage
) : UserRepository {

    private var linksListener: ListenerRegistration? = null

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        name: String,
        userName: String
    ): Response<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val newUser = User(
                    email = email,
                    userId = it.uid,
                    fullName = name,
                    userName = userName,
                    aboutYourself = "Not Added",
                    dob = "Not Added",
                    phone = "Not Added",
                    photo = "Not Added",
                    gender = "Not Added",
                    company = "Not Added"
                )
                saveUser(newUser)
            } ?: Response.Error(Exception("User creation failed"))
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Response<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                getUserData(it.uid)
            } ?: Response.Error(Exception("Sign-in failed"))
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun getUserData(userId: String): Response<User> {
        return try {
            val userRef = firestore.collection("Tappze").document(userId)
            val document = userRef.get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java) ?: User()
                Response.Success(user)
            } else {
                Response.Error(Exception("No user document found"))
            }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    private suspend fun saveUser(user: User): Response<Unit> {
        return try {
            val userRef = firestore.collection("Tappze").document(user.userId!!)
            userRef.set(user).await()

            val linksRef = userRef.collection("SocialLinks").document("Links")
            linksRef.set(Links()).await()

            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun getExistingLinks(userId: String): Response<Links> {
        return try {
            val linksRef = firestore.collection("Tappze").document(userId)
                .collection("SocialLinks").document("Links")

            val document = linksRef.get().await()

            if (document.exists()) {
                val links = document.toObject(Links::class.java) ?: Links()
                Response.Success(links)
            } else {
                Response.Error(Exception("No links document found"))
            }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun addLink(userId: String, appName: String, link: String): Response<Unit> {
        return try {
            val existingLinksResponse = getExistingLinks(userId)
            if (existingLinksResponse is Response.Error) {
                return Response.Error(existingLinksResponse.exception)
            }

            val existingLinks = (existingLinksResponse as Response.Success).data
            val updatedLinks = existingLinks.links.toMutableMap()
            updatedLinks[appName] = link

            val linksRef = firestore.collection("Tappze").document(userId)
                .collection("SocialLinks").document("Links")

            linksRef.set(Links(updatedLinks)).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    private suspend fun updateUser(userData: User) {
        userData.userId?.let { firestore.collection("Tappze").document(it).set(userData).await() }
    }

    override suspend fun uploadImageAndUpdateUser(userData: User): Response<Unit> {
        val storageReference = firebaseStorage.reference
        val userImagePath = "Photos/${userData.userId}"

        val ref = storageReference.child(userImagePath)

        return try {
            val imageExists = try {
                ref.metadata.await()
                true
            } catch (e: Exception) {
                false
            }

            userData.photo?.let {
                val uploadTask = ref.putFile(it.toUri()).await()
                val downloadUrl = ref.downloadUrl.await()
                userData.photo = downloadUrl.toString()
            }

            updateUser(userData)
            preferencesHelper.updateUser { currentUser ->
                currentUser.copy(
                    userName = userData.userName,
                    aboutYourself = userData.aboutYourself,
                    photo = userData.photo,
                    company = userData.company,
                    gender = userData.gender,
                    dob = userData.dob,
                    phone = userData.phone
                )
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override fun generateQRCode(url: String): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) -0x1000000 else -0x1)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun sendChangePasswordEmail(email: String): Response<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun deleteLink(linkToDelete: String): Response<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Response.Error(Exception("User not authenticated"))

            val linksRef = firestore.collection("Tappze").document(userId)
                .collection("SocialLinks").document("Links")

            val updateMap = mapOf("links.$linkToDelete" to FieldValue.delete())

            linksRef.update(updateMap).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override fun observeLinks(userId: String, onLinksUpdated: (Links?) -> Unit) {
        val linksRef = firestore.collection("Tappze").document(userId)
            .collection("SocialLinks").document("Links")

        // Add the real-time listener
        linksListener = linksRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle the error if needed
                onLinksUpdated(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val links = snapshot.toObject(Links::class.java)
                onLinksUpdated(links)
            } else {
                onLinksUpdated(null)
            }
        }
    }

    // Method to stop observing links when no longer needed
    override fun stopObservingLinks() {
        linksListener?.remove()
    }
}