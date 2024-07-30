package com.example.tappze.com.example.tappze.di

import android.content.Context
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppIntentModule {
    @Provides
    @Singleton
    fun provideAppIntentUtil(@ApplicationContext context: Context): AppIntentUtil {
        return AppIntentUtil(context)
    }
}