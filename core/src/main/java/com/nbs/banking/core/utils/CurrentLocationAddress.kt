package com.nbs.banking.core.utils

import com.nbs.banking.core.extensions.emptyString


data class CurrentLocationAddress(
    val subdistrict: String = emptyString(),
    val village: String = emptyString(),
    val province: String = emptyString(),
    val city: String = emptyString(),
)
