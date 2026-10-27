package com.nbs.banking.core.utils

import androidx.annotation.Keep

@Keep
enum class LocationPrecheckResult {
    READY,
    PERMISSION_DENIED,
    LOCATION_SERVICE_DISABLED,
}
