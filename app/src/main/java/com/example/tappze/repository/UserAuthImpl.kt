package com.example.tappze.com.example.tappze.repository

import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserAuthImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserAuthentication {
    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        name: String,
        userName: String
    ): Response<Unit> {
        if (email.isEmpty()) {
            return Response.Error(IllegalArgumentException("Email cannot be null or empty"))
        }

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

    private suspend fun saveUser(user: User): Response<Unit> {
        return try {
            val userRef = user.userId?.let { firestore.collection("Tappze").document(it) }
            userRef?.set(user)?.await()
            val linksRef = userRef?.collection("SocialLinks")?.document("Links")
            linksRef?.set(Links())?.await()
            Response.Success(Unit)
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
    private suspend fun getUserData(userId: String): Response<User> {
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

    override suspend fun sendChangePasswordEmail(email: String): Response<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

}