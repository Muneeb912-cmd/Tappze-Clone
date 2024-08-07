package com.example.tappze.com.example.tappze.repository

import android.graphics.Bitmap
import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SocialLinkRepoImpl@Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : SocialLinksRepository {

    private var linksListener: ListenerRegistration? = null
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

    override fun stopObservingLinks() {
        linksListener?.remove()
    }
}