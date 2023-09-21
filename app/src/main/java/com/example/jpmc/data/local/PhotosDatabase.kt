package com.example.jpmc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jpmc.data.local.dao.WeatherDao
import com.example.jpmc.data.local.entity.Weather
import javax.inject.Singleton

@Singleton
@Database(entities = [Weather::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun photosDao(): WeatherDao
}
