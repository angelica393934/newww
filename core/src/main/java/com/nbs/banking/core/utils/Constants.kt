package com.nbs.banking.core.utils

object Constants {
    const val UNKNOWN_CODE = -1
    const val STATUS_CODE_SUCCESS = 200
    const val STATUS_CODE_CREATED = 201
    const val STATUS_CODE_BAD_REQUEST = 400
    const val STATUS_CODE_UNAUTHORIZED = 401
    const val STATUS_CODE_FORBIDDEN = 403
    const val STATUS_CODE_NOT_FOUND = 404
    const val STATUS_CODE_NOT_ACCEPTABLE = 406
    const val STATUS_CODE_PROXY_AUTHENTICATION_REQUIRED = 407
    const val STATUS_CODE_GONE = 410
    const val STATUS_UPGRADE_REQUIRED = 426
    const val STATUS_CODE_TOO_MANY_REQUESTS = 429
    const val STATUS_CODE_INTERNAL_SERVER_ERROR = 500
    const val STATUS_CODE_SERVICE_UNAVAILABLE = 503
    const val STATUS_CODE_NETWORK_TIMEOUT = 504
    const val STATUS_CODE_SYSTEM_MAINTENANCE = 599
    const val AMEX_MAX_NUMBER = 15
    const val VIMAS_MAX_NUMBER = 16
    const val AMEX_CVV_MAX_NUMBER = 4
    const val VIMAS_CVV_MAX_NUMBER = 3
    const val AMEX_BIN_NUMBER_THIRTY_FOUR = "34"
    const val AMEX_BIN_NUMBER_THIRTY_SEVEN = "37"
    const val MASKING_MIN_RANGE = 6
    const val MASKING_MAX_RANGE = 11
    const val EMERGENCY_PHONE_MAX_LENGTH = 13
    const val PDF_EXTENSION_TYPE = ".pdf"
    const val HTML_EXTENSION_TYPE = ".html"
    const val JPG_EXTENSION_TYPE = ".jpg"
    const val TRANSACTION_CODE = "TransactionCode"
    const val VERSION_ONE = "v1"
    const val VERSION_TWO = "v2"
    const val BULLET = "•"

    const val NPWP_MAX_LENGTH = 16
    const val NPWP_MIN_LENGTH = 15
    const val PHONE_MIN_LENGTH_8 = 8
    const val PHONE_MAX_LENGTH_13 = 13
    const val DEFAULT_MAX_CHAR = 256
    const val NIK_LENGTH = 16
    const val BYTE_SIZE_2MB = 2_000_000
    const val NUMBER_EIGHT = "8"
    const val PDF = "pdf"
    const val IMG_SUFFIX = "IMG"
    val IMAGE_PDF_FILE_TYPE = arrayOf("image/*", "application/pdf")
    val ALLOWED_MIME_TYPES = listOf("jpg", "png", "pdf")
    const val HTTP_PROTOCOL = "http://"
    const val HTTPS_PROTOCOL = "https://"
}
