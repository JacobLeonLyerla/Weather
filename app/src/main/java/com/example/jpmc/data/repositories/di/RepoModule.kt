package com.example.jpmc.data.repositories.di

import android.location.Geocoder
import com.example.jpmc.annotations.DefaultScope
import com.example.jpmc.data.local.dao.WeatherDao
import com.example.jpmc.data.remote.services.WeatherService
import com.example.jpmc.data.repositories.WeatherRepo
import com.example.jpmc.data.repositories.WeatherRepoImpl
import com.example.jpmc.utils.loction.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)

    @Provides
    fun provideWeatherRepo(
        service: WeatherService,
        locationTracker: LocationTracker,
        geocoder: Geocoder,
        weatherDao: WeatherDao,
        @DefaultScope coroutineScope: CoroutineScope
    ): WeatherRepo =
        WeatherRepoImpl(
            service,
            locationTracker,
            weatherDao,
            geocoder,
            coroutineScope
        )
}
