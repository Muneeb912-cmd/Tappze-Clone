package com.example.tappze.com.example.tappze.di

import com.example.tappze.com.example.tappze.broadcast.StatusObserver
import com.example.tappze.com.example.tappze.broadcast.StatusObserverImp
import com.example.tappze.com.example.tappze.broadcast.StatusReceiver
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
    fun provideStatusObserver(): StatusObserver {
        return StatusObserverImp()
    }

    @Provides
    @Singleton
    fun provideNetworkAndBatteryStatusReceiver(statusObserver: StatusObserver): StatusReceiver {
        return StatusReceiver(statusObserver)
    }
}