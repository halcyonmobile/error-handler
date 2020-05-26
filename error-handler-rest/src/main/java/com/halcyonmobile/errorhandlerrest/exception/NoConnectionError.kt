package com.halcyonmobile.errorhandlerrest.exception

/**
 * Error thrown when the HTTP request couldn't be performed because the device isn't connected to a network.
 */
class NoConnectionError(cause: Throwable? = null) : ConnectivityError(message = "Connection unavailable", cause = cause)