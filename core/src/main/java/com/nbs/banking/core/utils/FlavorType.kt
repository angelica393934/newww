/**
 *  Copyright (c) 2025 PT. Bank Danamon Indonesia Tbk Private License.
 *
 *  Permission is hereby granted to employees and affiliates of PT. Bank Danamon Indonesia Tbk
 *  (the enterprise) to use the DBank Pro source code within the enterprise's private repositories
 *  for internal business purposes only.
 *
 *  Redistribution of the source code outside of the enterprise's private repositories is
 *  prohibited. The source code may not be shared, sold, or transferred to any third party.
 *
 *  The source code may be modified for internal use within the enterprise. All modified
 *  versions of the source code remain the property of the enterprise.
 *
 *  The source code may include open-source third-party components, each of which is subject
 *  to its respective open-source license. The terms of each open-source license apply to the
 *  corresponding component and prevail over this license with respect to that component.
 *
 *  All proprietary rights, including intellectual property rights, remain with the enterprise.
 *  Use of the enterprise's name, logo, or trademarks is not permitted.
 *
 *  The source code is provided "as is," without warranty of any kind. The enterprise shall not
 *  be liable for any damages arising from the use of the source code.
 *
 *  This license is effective until terminated. The enterprise reserves the right to terminate
 *  this license if its terms are violated.
 *
 *  By using this source code, you acknowledge that you have read, understood, and agree to be
 *  bound by the terms and conditions of this license.
 *
 *
 *  Created by PT. Bank Danamon Indonesia Tbk on 2025-05-19.
 */

package com.nbs.banking.core.utils


enum class FlavorType {
    DEV,
    STAGING,
    PROD,
    ;

//    companion object {
//        fun isDev() = BuildConfig.FLAVOR == DEV.name.lowercase()
//
//        fun isStaging() = BuildConfig.FLAVOR == STAGING.name.lowercase()
//
//        fun isProd() = BuildConfig.FLAVOR == PROD.name.lowercase()
//
//        fun isDebuggingFlavor() = BuildConfig.FLAVOR in listOf(
//            DEV.name.lowercase(),
//            STAGING.name.lowercase(),
//        )
//
//        fun <T> provideByFlavor(
//            dev: () -> T,
//            sit: () -> T,
//            uat: () -> T,
//            preprod: () -> T,
//            pt: () -> T,
//            prod: () -> T,
//        ): T = when (BuildConfig.FLAVOR) {
//            DEV.name.lowercase() -> dev.invoke()
//            STAGING.name.lowercase() -> sit.invoke()
//            PROD.name.lowercase() -> prod.invoke()
//            else -> throw IllegalArgumentException("Unknown flavor: ${BuildConfig.FLAVOR}")
//        }
////
//        fun <T> getByFlavor(
//            dev: T,
//            sit: T,
//            uat: T,
//            preprod: T,
//            pt: T,
//            prod: T,
//        ): T = when (BuildConfig.FLAVOR) {
//            DEV.name.lowercase() -> dev
//            STAGING.name.lowercase() -> sit
//            PROD.name.lowercase() -> prod
//            else -> throw IllegalArgumentException("Unknown flavor: ${BuildConfig.FLAVOR}")
//        }
//    }
//
//    fun doOnFlavor(
//        dev: () -> Unit = {},
//        staging: () -> Unit = {},
//        prod: () -> Unit = {},
//    ) {
//        when (this) {
//            DEV -> dev.invoke()
//            STAGING -> staging.invoke()
//            PROD -> prod.invoke()
//        }
//    }
}
