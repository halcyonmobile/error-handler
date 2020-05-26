package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorhandlercore.DataLayerException

/**
 * Signals that an error of some sort occurred during the remote call. This class is a general class of exceptions produced by a failure during the remote call.
 *
 * @see ApiError
 * @see ErrorPayloadParsingException
 * @see ConnectivityError
 * @see ParsingError
 * @see ServerCommunicationException
 * @see InternalServerError
 */
open class RemoteException(message: String? = null, cause: Throwable? = null) : DataLayerException(message = message, cause = cause)