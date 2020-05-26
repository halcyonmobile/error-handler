package com.halcyonmobile.errorhandlerrest.exception

/**
 * Parent exception for all connection related errors.
 *
 * @see NoConnectionError
 * @see ConnectionTimedOutError
 */
open class ConnectivityError(message: String? = null, cause: Throwable? = null) : RemoteException(message, cause)