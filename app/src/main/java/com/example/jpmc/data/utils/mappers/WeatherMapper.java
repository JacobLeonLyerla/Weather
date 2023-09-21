package com.example.jpmc.data.utils.mappers;

import com.example.jpmc.data.local.entity.Weather;
import com.example.jpmc.data.remote.response.WeatherResponse;

public class WeatherMapper {
    public static Weather mapToWeatherEntity(WeatherResponse response) {
        return new Weather(
                response.getName(),
                response.getMain().getTemp(),
                response.getMain().getHumidity(),
                response.getWind().getSpeed(),
                response.getWeather().get(0).getDescription(),
                response.getWeather().get(0).getIcon()
        );
    }
}
