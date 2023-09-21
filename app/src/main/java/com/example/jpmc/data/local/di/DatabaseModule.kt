package com.example.jpmc.data.local.di

import android.content.Context
import androidx.room.Room
import com.example.jpmc.data.local.WeatherDatabase
import com.example.jpmc.data.local.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "weather_database"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePhotosDao(database: WeatherDatabase): WeatherDao {
        return database.photosDao()
    }
}
