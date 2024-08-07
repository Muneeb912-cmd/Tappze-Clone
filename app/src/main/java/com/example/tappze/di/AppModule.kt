package com.example.tappze.di

import com.example.tappze.com.example.tappze.repository.SocialLinkRepoImpl
import com.example.tappze.com.example.tappze.repository.SocialLinksRepository
import com.example.tappze.com.example.tappze.repository.UserAuthImpl
import com.example.tappze.com.example.tappze.repository.UserAuthentication
import com.example.tappze.com.example.tappze.repository.UserRepoImpl
import com.example.tappze.com.example.tappze.utils.PreferencesHelper
import com.example.tappze.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        preferencesHelper: PreferencesHelper,
        firebaseStorage: FirebaseStorage,
    ): UserRepository = UserRepoImpl(firestore, preferencesHelper,firebaseStorage)

    @Provides
    @Singleton
    fun provideUserAuthentication(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): UserAuthentication = UserAuthImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideManageSocialLinks(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): SocialLinksRepository = SocialLinkRepoImpl(auth, firestore)
}
