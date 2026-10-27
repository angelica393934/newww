package com.nbs.banking.core.config.provider

//import com.danamon.mobile.core.config.ConfigDetail
//import com.danamon.mobile.core.config.FlagConfig
//import com.nbs.banking.core.config.key.ConfigKey
//import com.nbs.banking.core.config.key.FlagKey
//import io.getunleash.android.DefaultUnleash
//import io.getunleash.android.data.UnleashContext
//import java.util.UUID
//import timber.log.Timber
//
//class UnleashConfigProvider(private val unleash: DefaultUnleash) : ConfigProvider {
//    init {
//        // Temporary suppress
//        // TODO: replace with actual context
//        @Suppress("UnusedPrivateProperty")
//        val initialContext = UnleashContext
//            .newBuilder()
//            .sessionId(UUID.randomUUID().toString())
//            .addProperty("ganteng", "true")
//            .build()
//
////        unleash.start(
////            eventListeners = listOf(
////                object : UnleashReadyListener {
////                    override fun onReady() {
////                        Timber.tag("DUDIDAM").d("Unleash is ready ${unleash.isReady()}")
////                    }
////                },
////            ),
////        )
////        unleash.setContext(initialContext)
//    }
//
//    override fun getConfigVersion(): Int = 0
//
//    override fun getString(key: ConfigKey): String {
//        val variant = unleash.getVariant(key.key)
//        return variant.payload?.getValueAsString() ?: ""
//    }
//
//    override fun getDouble(key: ConfigKey): Double {
//        val variant = unleash.getVariant(key.key)
//        return variant.payload?.getValueAsDouble() ?: 0.0
//    }
//
//    override fun getInt(key: ConfigKey): Int {
//        val variant = unleash.getVariant(key.key)
//        return variant.payload?.getValueAsInt() ?: 0
//    }
//
//    override fun getLong(key: ConfigKey): Long {
//        val variant = unleash.getVariant(key.key)
//        return variant.payload?.getValueAsString()?.toLongOrNull() ?: 0L
//    }
//
//    override fun <T> getObject(key: ConfigKey, type: Class<T>): T? {
//        val variant = unleash.getVariant(key.key)
//        return variant.payload?.value.fromJsonOrNull(type)
//    }
//
//    override fun getFlagConfig(flag: FlagKey, default: Boolean): FlagConfig {
//        Timber.tag("DUDIDAM").d(unleash.isEnabled(flag.key, true).toString())
//        Timber.tag("DUDIDAM").d(unleash.getVariant(flag.key).toString())
//        val variant = unleash.getVariant(flag.key)
//        val configDetail: ConfigDetail? = variant.payload?.value.fromJsonOrNull()
//        val flagConfig = configDetail?.let {
//            FlagConfig(
//                enabled = variant.featureEnabled,
//                configDetail = it,
//            )
//        } ?: FlagConfig()
//        return flagConfig
//    }
//}
