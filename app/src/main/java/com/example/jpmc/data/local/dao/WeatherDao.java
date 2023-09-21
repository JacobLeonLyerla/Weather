package com.example.jpmc.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.jpmc.data.local.entity.Weather;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replace(Weather weather);

    @Query("DELETE FROM weather")
    void deleteAll();

    @Query("SELECT * FROM weather LIMIT 1")
    Flow<Weather> getCurrentWeather();
}
