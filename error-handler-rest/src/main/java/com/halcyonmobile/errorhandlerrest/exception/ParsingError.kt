package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorhandlerrest.logger.Loggable

/**
 * Represents an error occurred while parsing the response. It is thrown is case of a successful response where the response body couldn't be parsed.
 *
 * Note that this error is thrown only if the consumer provides a [com.halcyonmobile.errorhandlerrest.SerializationExceptionConverter] which makes the
 * conversion between any serialization related exception to a [SerializationException].
 */
class ParsingError(
    message: String = "",
    cause: Throwable? = null
) : RemoteException(message = message, cause = cause), Loggable