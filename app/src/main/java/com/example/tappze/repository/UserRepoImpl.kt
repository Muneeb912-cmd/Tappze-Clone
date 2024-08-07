package com.example.tappze.com.example.tappze.repository

import androidx.core.net.toUri
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val preferencesHelper: PreferencesHelper,
    private val firebaseStorage: FirebaseStorage
) : UserRepository {



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


}