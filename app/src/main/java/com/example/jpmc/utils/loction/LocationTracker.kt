package com.example.jpmc.utils.loction

import android.location.Location

interface LocationTracker {

    suspend fun getCurrentLocation(): Location?

}