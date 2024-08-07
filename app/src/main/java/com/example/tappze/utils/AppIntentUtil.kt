package com.example.tappze.com.example.tappze.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppIntentUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun openInstagramProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$id")).apply {
            setPackage("com.instagram.android")
        }
        return if (isAppInstalled("com.instagram.android")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$id"))
    }

    fun openFacebookProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/$id")).apply {
            setPackage("com.facebook.katana")
        }
        return if (isAppInstalled("com.facebook.katana")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$id"))
    }

    fun openTikTokProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@$id")).apply {
            setPackage("com.zhiliaoapp.musically")
        }
        return if (isAppInstalled("com.zhiliaoapp.musically")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@$id"))
    }

    fun openWhatsappChat(phoneNumber: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phoneNumber")).apply {
            setPackage("com.whatsapp")
        }
        return if (isAppInstalled("com.whatsapp")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phoneNumber"))
    }

    fun openLinkedInProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/$id")).apply {
            setPackage("com.linkedin.android")
        }
        return if (isAppInstalled("com.linkedin.android")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/$id"))
    }

    fun openTelegramChat(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$id")).apply {
            setPackage("org.telegram.messenger")
        }
        return if (isAppInstalled("org.telegram.messenger")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$id"))
    }

    fun openSnapchatProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.snapchat.com/add/$id")).apply {
            setPackage("com.snapchat.android")
        }
        return if (isAppInstalled("com.snapchat.android")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.snapchat.com/add/$id"))
    }

    fun openXProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com/$id")).apply {
            setPackage("com.x.android")
        }
        return if (isAppInstalled("com.x.android")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com/$id"))
    }

    fun openWebsite(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        return Intent.createChooser(intent, "Choose browser")
    }


    fun openYouTubeChannel(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/$id")).apply {
            setPackage("com.google.android.youtube")
        }
        return if (isAppInstalled("com.google.android.youtube")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/$id"))
    }

    fun openSpotifyPlaylist(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:$id")).apply {
            setPackage("com.spotify.android.music")
        }
        return if (isAppInstalled("com.spotify.android.music")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/playlist/$id"))
    }

    fun sendEmail(emailAddress: String): Intent? {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, "Subject")
            putExtra(Intent.EXTRA_TEXT, "Body")
        }

        val gmailPackage = "com.google.android.gm"
        if (isAppInstalled(gmailPackage)) {
            intent.setPackage(gmailPackage)
        } else {
            println("Gmail app is not installed. Falling back to default email client.")
        }
        return if (intent.resolveActivity(context.packageManager) != null) {
            intent
        } else {
            println("No email app available to handle the intent.")
            null
        }
    }



    fun openPayPal(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("paypal://profile/$id")).apply {
            setPackage("com.paypal.android.p2pmobile")
        }
        return if (isAppInstalled("com.paypal.android.p2pmobile")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/$id"))
    }

    fun openPinterestProfile(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("pinterest://user/$id")).apply {
            setPackage("com.pinterest")
        }
        return if (isAppInstalled("com.pinterest")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/$id"))
    }

    fun openSkypeCall(id: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("skype:$id?call")).apply {
            setPackage("com.skype.raider")
        }
        return if (isAppInstalled("com.skype.raider")) intent else Intent(Intent.ACTION_VIEW, Uri.parse("https://join.skype.com/$id"))
    }

    fun openCalendly(url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
    fun openPhone(number: String): Intent {
        val formattedNumber = Uri.encode(number)
        return Intent(Intent.ACTION_DIAL, Uri.parse("tel:$formattedNumber"))
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    fun buyTappzeCard(): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tappze.com/shop/"))
        return Intent.createChooser(intent, "Choose browser")
    }

    fun tappzeTermsAndServices(): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tappze.com/"))
        return Intent.createChooser(intent, "Choose browser")
    }

    fun tappzePrivacyPolicy(): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tappze.com/privacy_policy"))
        return Intent.createChooser(intent, "Choose browser")
    }

}

