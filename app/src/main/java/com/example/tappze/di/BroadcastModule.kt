package com.example.tappze.com.example.tappze.di

import com.example.tappze.com.example.tappze.broadcast.BatteryStatusObserver
import com.example.tappze.com.example.tappze.broadcast.BatteryStatusReceiver
import com.example.tappze.com.example.tappze.broadcast.NetworkStatusObserver
import com.example.tappze.com.example.tappze.broadcast.NetworkStatusReceiver
import com.example.tappze.com.example.tappze.broadcast.StatusObserverImp
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
    fun provideStatusObserverImp(): StatusObserverImp {
        return StatusObserverImp()
    }

    @Provides
    fun provideNetworkStatusObserver(statusObserverImp: StatusObserverImp): NetworkStatusObserver {
        return statusObserverImp
    }

    @Provides
    fun provideBatteryStatusObserver(statusObserverImp: StatusObserverImp): BatteryStatusObserver {
        return statusObserverImp
    }

    @Provides
    fun provideNetworkStatusReceiver(networkStatusObserver: NetworkStatusObserver): NetworkStatusReceiver {
        return NetworkStatusReceiver(networkStatusObserver)
    }

    @Provides
    fun provideBatteryStatusReceiver(batteryStatusObserver: BatteryStatusObserver): BatteryStatusReceiver {
        return BatteryStatusReceiver(batteryStatusObserver)
    }
}