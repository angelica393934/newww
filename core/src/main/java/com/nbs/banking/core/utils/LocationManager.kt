package com.nbs.banking.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager as AndroidLocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

class LocationManager(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
) : LocationTracker {
    override fun hasLocationPermission(): Boolean {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        return hasAccessFineLocationPermission || hasAccessCoarseLocationPermission
    }

    override suspend fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE,
        ) as AndroidLocationManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER)
        }
    }

    override suspend fun checkLocationPrecheck(): LocationPrecheckResult = when {
        !hasLocationPermission() -> LocationPrecheckResult.PERMISSION_DENIED
        !isLocationServiceEnabled() -> LocationPrecheckResult.LOCATION_SERVICE_DISABLED
        else -> LocationPrecheckResult.READY
    }

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission() || !isLocationServiceEnabled()) {
            return null
        }

        val lastLocation = getLastLocation()
        if (lastLocation != null) {
            return lastLocation
        }

        return withTimeoutOrNull(GET_CURRENT_LOCATION_TIMEOUT) {
            runCatching {
                suspendCancellableCoroutine { cont ->
                    val cancellationTokenSource = CancellationTokenSource()

                    cont.invokeOnCancellation {
                        cancellationTokenSource.cancel()
                    }

                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token,
                    ).apply {
                        addOnSuccessListener { location ->
                            if (cont.isActive) {
                                cont.resume(location) {}
                            }
                        }

                        addOnFailureListener { exception ->
                            Timber.e(exception)
                            if (cont.isActive) {
                                cont.resume(null) {}
                            }
                        }

                        addOnCanceledListener {
                            if (cont.isActive) {
                                cont.cancel()
                            }
                        }
                    }
                }
            }.onFailure { e ->
                Timber.e(e)
            }.getOrNull()
        }
    }

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLastLocation(): Location? {
        if (!hasLocationPermission() || !isLocationServiceEnabled()) {
            return null
        }

        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result) {} // Resume coroutine with location result
                    } else {
                        cont.resume(null) {} // Resume coroutine with null location result
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it) {} // Resume coroutine with location result
                }
                addOnFailureListener {
                    cont.resume(null) {} // Resume coroutine with null location result
                }
                addOnCanceledListener {
                    cont.cancel() // Cancel the coroutine
                }
            }
        }
    }

    override fun geocode(address: String, geocodeResultListener: (addressList: List<Address>?) -> Unit) {
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runCatching {
                geocoder.getFromLocationName(
                    address,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            geocodeResultListener(addresses)
                        }

                        override fun onError(errorMessage: String?) {
                            Timber.e(errorMessage)
                            geocodeResultListener(null)
                        }
                    },
                )
            }.onFailure { throwable ->
                Timber.e(throwable)
                geocodeResultListener(null)
            }
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                runCatching {
                    val result = geocoder.getFromLocationName(address, 1)
                    geocodeResultListener(result)
                }.onFailure { e ->
                    Timber.e(e)
                    geocodeResultListener(null)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun reverseGeocode(latitude: Double, longitude: Double): CurrentLocationAddress {
        if (latitude == 0.0 && longitude == 0.0) {
            return CurrentLocationAddress()
        }

        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context)

            runCatching {
                val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(
                            latitude,
                            longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    if (cont.isActive) {
                                        cont.resume(addresses.firstOrNull()) {}
                                    }
                                }

                                override fun onError(errorMessage: String?) {
                                    if (cont.isActive) {
                                        cont.resume(null) {}
                                    }
                                }
                            },
                        )
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
                }

                address.toCurrentLocationAddress()
            }.onFailure { e ->
                Timber.e(e)
            }.getOrDefault(CurrentLocationAddress())
        }
    }

    private fun Address?.toCurrentLocationAddress(): CurrentLocationAddress {
        if (this == null) return CurrentLocationAddress()

        return CurrentLocationAddress(
            subdistrict = locality.orEmpty(),
            village = subLocality.orEmpty(),
            province = adminArea.orEmpty(),
            city = subAdminArea.orEmpty(),
        )
    }

    companion object {
        private val GET_CURRENT_LOCATION_TIMEOUT = 10.seconds
    }
}
