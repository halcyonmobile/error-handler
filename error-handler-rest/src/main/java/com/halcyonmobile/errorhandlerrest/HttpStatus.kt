package com.halcyonmobile.errorhandlerrest

import java.net.HttpURLConnection

/**
 * Constants representing HTTP status codes.
 */
object HttpStatus {

    const val UNAUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED
    const val FORBIDDEN = HttpURLConnection.HTTP_FORBIDDEN
    const val NOT_FOUND = HttpURLConnection.HTTP_NOT_FOUND
    const val INTERNAL_SERVER_ERROR = HttpURLConnection.HTTP_INTERNAL_ERROR
}

/**
 * Helper method for comparing an Integer with [HttpStatus.NOT_FOUND] value.
 */
fun Int.isNotFound() = this == HttpStatus.NOT_FOUND