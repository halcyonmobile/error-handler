package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorhandlerrest.logger.Loggable

/**
 * Represents problems which occurs while talking to the server or if an unexpected error occurs creating the request
 * or decoding the response (other than deserialization related issues which is represented by [ParsingError].
 *
 * For detailed exception types, see the [retrofit2.Call.execute] method or [retrofit2.Callback.onFailure] method.
 */
class ServerCommunicationException(
    message: String? = "",
    cause: Throwable? = null
) : RemoteException(message = message, cause = cause), Loggable