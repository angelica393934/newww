package com.nbs.banking.core.error

import com.nbs.banking.core.utils.Constants.STATUS_CODE_BAD_REQUEST
import com.nbs.banking.core.utils.Constants.STATUS_CODE_FORBIDDEN
import com.nbs.banking.core.utils.Constants.STATUS_CODE_GONE
import com.nbs.banking.core.utils.Constants.STATUS_CODE_INTERNAL_SERVER_ERROR
import com.nbs.banking.core.utils.Constants.STATUS_CODE_NETWORK_TIMEOUT
import com.nbs.banking.core.utils.Constants.STATUS_CODE_NOT_ACCEPTABLE
import com.nbs.banking.core.utils.Constants.STATUS_CODE_SERVICE_UNAVAILABLE
import com.nbs.banking.core.utils.Constants.STATUS_CODE_SUCCESS
import com.nbs.banking.core.utils.Constants.STATUS_CODE_SYSTEM_MAINTENANCE
import com.nbs.banking.core.utils.Constants.STATUS_CODE_UNAUTHORIZED
import com.nbs.banking.core.utils.Constants.STATUS_UPGRADE_REQUIRED
import com.nbs.banking.core.utils.Constants.UNKNOWN_CODE

enum class StatusCode(val code: Int) {
    UNKNOWN(UNKNOWN_CODE),
    BAD_REQUEST(STATUS_CODE_BAD_REQUEST),
    UNAUTHORIZED(STATUS_CODE_UNAUTHORIZED),
    FORBIDDEN(STATUS_CODE_FORBIDDEN),
    NOT_ACCEPTABLE(STATUS_CODE_NOT_ACCEPTABLE),
    INTERNAL_SERVER_ERROR(STATUS_CODE_INTERNAL_SERVER_ERROR),
    SERVICE_NOT_AVAILABLE(STATUS_CODE_SERVICE_UNAVAILABLE),
    NETWORK_TIMEOUT(code = STATUS_CODE_NETWORK_TIMEOUT),
    SUCCESS(STATUS_CODE_SUCCESS),
    SYSTEM_MAINTENANCE(STATUS_CODE_SYSTEM_MAINTENANCE),
    DEVICE_CHANGE(STATUS_CODE_GONE),
    UPGRADE_REQUIRED(STATUS_UPGRADE_REQUIRED),
}

enum class ErrorType {
    UNAUTHORIZED,
    SYSTEM_MAINTENANCE,
    DEVICE_CHANGED,
    UPGRADE_REQUIRED,
    TECHNICAL_ISSUE,
    FEATURE_UNAVAILABLE,
    INVALID_DEVICE_TIME,
    TIME_OUT,
    CUSTOM_ERROR,
    MPIN_BLOCKED,
    INVALID_CONTACT_TRANSACTION,
    CANNOT_PROCESS_TRANSACTION,
}

object TagErrorConstant {
    const val TAG_DATABASE_EXCEPTION = "Database Exception: "
    const val TAG_CANCELLATION_EXCEPTION = "Cancellation Exception: "
    const val TAG_NETWORK_EXCEPTION = "Network Exception: "
    const val TAG_MALFORMED_JSON_EXCEPTION = "Malformed JSON Exception: "
    const val TAG_UNKNOWN_HOST_EXCEPTION = "Unknown Host: "
    const val TAG_SOCKET_EXCEPTION = "Socket Exception: "
    const val TAG_JSON_PARSING_EXCEPTION = "Failed to parse JSON: "
    const val TAG_HTTP_EXCEPTION = "HTTP Exception: "
    const val TAG_TIMEOUT_EXCEPTION = "Socket Timeout Exception: "
    const val TAG_SSL_EXCEPTION = "SSL Handshake Exception: "
    const val TAG_SSL_PEER_UNVERIFIED_EXCEPTION = "SSL Unexpected Exception: "
    const val TAG_API_EXCEPTION = "API Exception: "
    const val TAG_ILLEGAL_EXCEPTION = "API Exception: "
    const val TAG_UNAUTHORIZED_EXCEPTION = "Unauthorized Exception: "
    const val TAG_SYSTEM_MAINTENANCE_EXCEPTION = "System Maintenance Exception: "
    const val TAG_DEVICE_CHANGE_EXCEPTION = "Device Change Exception: "
    const val TAG_TECHNICAL_ISSUE_EXCEPTION = "Technical Issue Exception: "
    const val TAG_CLOSED_CALL_EXCEPTION = "Closed Call Exception: "
}
