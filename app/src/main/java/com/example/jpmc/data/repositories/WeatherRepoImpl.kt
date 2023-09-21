package com.example.jpmc.data.repositories

import android.location.Geocoder
import android.location.Location
import com.example.jpmc.data.local.dao.WeatherDao
import com.example.jpmc.data.local.entity.Weather
import com.example.jpmc.data.remote.services.WeatherService
import com.example.jpmc.data.utils.mappers.WeatherMapper
import com.example.jpmc.utils.DateUtility.isDataStale
import com.example.jpmc.utils.loction.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class WeatherRepoImpl(
    private val service: WeatherService,
    private val locationTracker: LocationTracker,
    private val weatherDao: WeatherDao,
    private val geocoder: Geocoder,
    private val repoScope: CoroutineScope
) : WeatherRepo {

    // Fetches current weather information. If the data is available locally and is not stale, returns it.
    // Otherwise, fetches new data from the service.
    override suspend fun getCurrentWeather(): Weather? {
        return withContext(repoScope.coroutineContext) {
            val localWeather = weatherDao.currentWeather.firstOrNull()

            if (localWeather != null) {
                // If data is less than 1 minute old (not stale), return it directly.
                if (!isDataStale(localWeather.timestamp)) {
                    return@withContext localWeather
                }

                // If data is stale, fetch new data using the city from stale data.
                val cityName =
                    localWeather.name // assuming you have a city field in your Weather entity
                fetchAndSaveWeather(cityName)
                return@withContext weatherDao.currentWeather.firstOrNull()
                // If, for some reason, the stale data doesn't have city info, you can decide to return stale data or fetch based on location.
                // return@withContext localWeather

            } else {
                // If no data in the database, use location to determine city and then fetch.
                locationTracker.getCurrentLocation()?.let { location ->
                    val cityNameFromLocation = getCityFromLocation(location)
                    if (cityNameFromLocation != null) {
                        fetchAndSaveWeather(cityNameFromLocation)
                        return@withContext weatherDao.currentWeather.firstOrNull()
                    }
                }
            }
            return@withContext null
        }
    }

    // Gets the city name based on the given Location object.
    // Utilizes Android's Geocoder to resolve the location to a city name.
    private suspend fun getCityFromLocation(location: Location): String? {
        return withContext(repoScope.coroutineContext) {
            // this is deprecated as of API level 33, however I could not get the alternative to work
            // if I had more time I would have looked into it deeper.
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.firstOrNull()?.locality
        }
    }
    // Fetches and saves weather information for the given city name.
    // Utilizes the WeatherService to get the data and then saves it to the local database.
    override suspend fun fetchAndSaveWeather(city: String) {
        val response = service.getWeatherByCity(city)
        val weatherEntity = WeatherMapper.mapToWeatherEntity(response)
        withContext(repoScope.coroutineContext) {
            weatherDao.deleteAll()
            weatherDao.replace(weatherEntity)
        }
    }

}
