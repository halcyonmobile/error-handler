package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorhandlerrest.logger.Loggable
import com.halcyonmobile.errorhandlerrest.util.HttpStatusCode

/**
 * Error representing the case when the HTTP call failed where the status code describes the root cause of the failure.
 * [com.halcyonmobile.errorhandlerrest.internal.NetworkExceptionToDataLayerExceptionConverter] will map the exception to this [ErrorPayloadParsingException]
 * when the error JSON payload can't be parsed. Server errors are represented by [InternalServerError] and because of this [statusCode] will represent
 * Client-Errors (4xx status codes).
 *
 *  @property statusCode HTTP status code with which the API responded.
 *
 *  @see InternalServerError
 */
class ErrorPayloadParsingException(
    val statusCode: HttpStatusCode,
    cause: Throwable? = null
) : RemoteException(null, cause), Loggable