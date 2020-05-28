package com.halcyonmobile.errorhandlerrest.exception

class ConnectionTimedOutError(cause: Throwable? = null) :
    ConnectivityError(message = "Time out", cause = cause)