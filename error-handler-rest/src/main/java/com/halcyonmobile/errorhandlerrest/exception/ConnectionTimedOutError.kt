package com.halcyonmobile.errorhandlerrest.exception

class ConnectionTimedOutError(override val message: String? = null, cause: Throwable? = null) : ConnectivityError(message, cause)