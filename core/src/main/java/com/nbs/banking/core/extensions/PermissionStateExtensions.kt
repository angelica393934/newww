package com.nbs.banking.core.extensions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isPermanentlyDenied(): Boolean = !status.isGranted && !status.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isPermanentlyDenied(): Boolean = !allPermissionsGranted && !shouldShowRationale
