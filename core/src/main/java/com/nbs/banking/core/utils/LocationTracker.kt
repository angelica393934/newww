package com.nbs.banking.core.utils

import android.location.Address
import android.location.Location

interface LocationTracker {
    fun hasLocationPermission(): Boolean

    suspend fun isLocationServiceEnabled(): Boolean

    suspend fun checkLocationPrecheck(): LocationPrecheckResult

    suspend fun getCurrentLocation(): Location?

    suspend fun getLastLocation(): Location?

    fun geocode(address: String, geocodeResultListener: (addressList: List<Address>?) -> Unit)

    suspend fun reverseGeocode(latitude: Double, longitude: Double): CurrentLocationAddress
}
