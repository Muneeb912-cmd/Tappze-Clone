package com.example.tappze.com.example.tappze.di

import com.example.tappze.R
import com.example.tappze.com.example.tappze.models.SocialLinks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StaticDataModule {
    @Provides
    @Singleton
    fun provideSocialLinks(): ArrayList<SocialLinks> {
        return arrayListOf(
            SocialLinks(R.drawable.ic_mobile, "Phone", isSaved = false),
            SocialLinks(R.drawable.ic_instagram, "Instagram", isSaved = false),
            SocialLinks(R.drawable.ic_facebook, "Facebook", isSaved = false),
            SocialLinks(R.drawable.ic_tiktok, "TikTok", isSaved = false),
            SocialLinks(R.drawable.ic_whatsapp, "Whatsapp", isSaved = false),
            SocialLinks(R.drawable.ic_linkedin, "LinkedIn", isSaved = false),
            SocialLinks(R.drawable.ic_telegram, "Telegram", isSaved = false),
            SocialLinks(R.drawable.ic_snapchat, "Snapchat", isSaved = false),
            SocialLinks(R.drawable.ic_twitter, "X", isSaved = false),
            SocialLinks(R.drawable.ic_internet, "Website", isSaved = false),
            SocialLinks(R.drawable.ic_youtube, "YouTube", isSaved = false),
            SocialLinks(R.drawable.ic_spotify, "Spotify", isSaved = false),
            SocialLinks(R.drawable.ic_gmail, "Email", isSaved = false),
            SocialLinks(R.drawable.ic_paypal, "PayPal", isSaved = false),
            SocialLinks(R.drawable.ic_pinterest, "Pinterest", isSaved = false),
            SocialLinks(R.drawable.ic_skype, "Skype", isSaved = false),
            SocialLinks(R.drawable.ic_calendly, "Calendly", isSaved = false)
        )
    }

    @Provides
    @Singleton
    fun provideHelpTextList(): Map<String, String> {
        return mapOf(
            "Phone" to "Add your phone number including your country code (e.g. +6581234567).",
            "Instagram" to "1. Open up your Instagram app and log into your account.\n" +
                    "2. Click on your profile picture at the bottom right corner.\n" +
                    "3. Your username will be shown at the very top of your profile (above your profile picture)\n" +
                    "4. Paste your username into the Instagram URL field.",
            "Facebook" to "If you are using the Facebook app on a mobile phone.\n" +
                    "1. Open up your Facebook app and log into your Facebook account.\n" +
                    "2. From the home page, click on the menu icon at the bottom right corner (It looks like three horizontal lines).\n" +
                    "3. Click on your profile picture to go to your profile page.\n" +
                    "4. Click on the Profile settings tab (three dots).\n" +
                    "5. Click on Copy Link to copy your full Facebook profile link.\n" +
                    "6. Paste the link into the Facebook URL field (e.g. www.facebook.com/your_fb_id).",
            "TikTok" to "1. Open up your TikTok app and log into your account.\n" +
                    "2. Click on your profile icon at the bottom right corner.\n" +
                    "3. Your username will be shown under your profile picture.\n" +
                    "4. Copy and paste the username into the TikTok URL field.",
            "Whatsapp" to "Add your phone number including your country code (e.g. +6581234567).",
            "LinkedIn" to "If you are using the LinkedIn app on a mobile phone.\n" +
                    "1. Open your LinkedIn app and log into your account\n" +
                    "2. From the home page, click on the profile picture in the top left.\n" +
                    "3. Click on the right side menu button (three dots).\n" +
                    "4. Select share via...\n" +
                    "5. Select Copy.\n" +
                    "6. Paste the copied link into the LinkedIn URL field.",
            "Telegram" to "1. Open up your Telegram app and log into your account.\n" +
                    "2. Click on settings located at the bottom right corner.\n" +
                    "3. Your username is shown under your contact number.\n" +
                    "4. Copy and paste the username into the Telegram URL field.",
            "Snapchat" to "1. Open up your Snapchat app and log into your account.\n" +
                    "2. Tap on your profile icon at the top left corner.\n" +
                    "3. Your username is shown right next to your Snapchat score.\n" +
                    "4. Copy and paste the username into the Snapchat URL field.",
            "X" to "No information available.",
            "Website" to "Enter the link to your website.",
            "Youtube" to "1. Sign in to your YouTube Studio account.\n" +
                    "2. From the Menu, select Customization Basic Info.\n" +
                    "3. Click the channel URL and copy the link.\n" +
                    "4. Paste the copied link into the YouTube URL field.",
            "Spotify" to "1. Search for your favorite Artist or Albums.\n" +
                    "2. Click on the three dots menu selection and select share.\n" +
                    "3. Select copy link.\n" +
                    "4. Paste the copied link into the Spotify URL field.",
            "Email" to "Input your email address.",
            "PayPal" to "1. Go to www.paypal.com and log in.\n" +
                    "2. Under your profile, select Get paypal.me.\n" +
                    "3. Create a paypal.me profile.\n" +
                    "4. Copy your created paypal.me link and paste it into the PayPal URL field.",
            "Pinterest" to "1. Open up your Pinterest app and log into your account.\n" +
                    "2. Click on your profile picture at the bottom right corner.\n" +
                    "3. Click on the three dots located at the top right corner.\n" +
                    "4. Select copy profile link.\n" +
                    "5. Paste the copied link into the Pinterest URL field.",
            "Skype" to "No information available.",
            "Calendly" to "1. Open up your Calendly app and log into your account.\n" +
                    "2. Copy the URL link below your name.\n" +
                    "3. Paste the copied link into the Calendly URL field."
        )
    }
    @Provides
    @Singleton
    fun provideImageResMap(): Map<String, Int> {
        return mapOf(
            "Phone" to R.drawable.ic_mobile,
            "Instagram" to R.drawable.ic_instagram,
            "Facebook" to R.drawable.ic_facebook,
            "TikTok" to R.drawable.ic_tiktok,
            "Whatsapp" to R.drawable.ic_whatsapp,
            "LinkedIn" to R.drawable.ic_linkedin,
            "Telegram" to R.drawable.ic_telegram,
            "Snapchat" to R.drawable.ic_snapchat,
            "X" to R.drawable.ic_twitter,
            "Website" to R.drawable.ic_internet,
            "YouTube" to R.drawable.ic_youtube,
            "Spotify" to R.drawable.ic_spotify,
            "Email" to R.drawable.ic_gmail,
            "PayPal" to R.drawable.ic_paypal,
            "Pinterest" to R.drawable.ic_pinterest,
            "Skype" to R.drawable.ic_skype,
            "Calendly" to R.drawable.ic_calendly,
        )
    }
}