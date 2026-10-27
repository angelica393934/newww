package com.nbs.banking.corenetwork.helper

import android.content.Context
import com.nbs.banking.core.extensions.emptyString
import okhttp3.CertificatePinner
import okhttp3.tls.HandshakeCertificates
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CertificateManager @Inject constructor(private val context: Context) {
    fun getCertificates(): HandshakeCertificates {
        val certificateBuilder = HandshakeCertificates.Builder()

        when {
//            BuildConfig.BASE_URL.contains(DEV_ENVIRONTMENT) -> {
//                certificateBuilder.addTrustedCertificate(pemStringToX509Certificate(FILENAME_CERTIFICATE_DEV))
//            }
//
//            BuildConfig.BASE_URL.contains(STAGING_ENVIRONTMENT) -> {
//                certificateBuilder.addTrustedCertificate(pemStringToX509Certificate(FILENAME_CERTIFICATE_SIT))
//                certificateBuilder.addTrustedCertificate(pemStringToX509Certificate(FILENAME_CERTIFICATE_AKAMAI_SIT))
//            }
//
//            BuildConfig.BASE_URL.contains(PROD_ENVIRONTMENT) -> {
//                certificateBuilder.addTrustedCertificate(pemStringToX509Certificate(FILENAME_CERTIFICATE_PROD))
//                certificateBuilder.addTrustedCertificate(pemStringToX509Certificate(FILENAME_CERTIFICATE_AKAMAI_PROD))
//            }
        }
        return certificateBuilder.build()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun pemStringToX509Certificate(fileName: String): X509Certificate {
        val certificateText = try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Timber.e(e)
            emptyString()
        }

        val cleanedPem = certificateText
            .replace(TEXT_BEGIN_CERTIFICATE, "")
            .replace(TEXT_END_CERTIFICATE, "")
            .replace("\n", "")
            .replace("\r", "")

        val decodedBytes = Base64.decode(cleanedPem)

        val certificateFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE)
        val inputStream = ByteArrayInputStream(decodedBytes)

        return certificateFactory.generateCertificate(inputStream) as X509Certificate
    }

    fun addCertificatePinner(): CertificatePinner = CertificatePinner.Builder().apply {
//        add(
//            BuildConfig.SHA_256_PIN,
//            BuildConfig.SHA_256_PIN_NEXT,
//        )
    }.build()

    companion object {
        private const val DEV_ENVIRONTMENT = "dev"
        private const val STAGING_ENVIRONTMENT = "staging"
        private const val PROD_ENVIRONTMENT = "www.dbank.co.id"
        private const val TEXT_BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----"
        private const val TEXT_END_CERTIFICATE = "-----END CERTIFICATE-----"
        private const val CERTIFICATE_FACTORY_TYPE = "X.509"
    }
}
