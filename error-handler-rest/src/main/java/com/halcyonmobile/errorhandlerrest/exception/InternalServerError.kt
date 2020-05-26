package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorhandlerrest.logger.Loggable

/**
 * Signals an internal error of the remote API, when 500 status code is returned.
 */
class InternalServerError(
    message: String = "",
    cause: Throwable? = null
) : RemoteException(message, cause), Loggable