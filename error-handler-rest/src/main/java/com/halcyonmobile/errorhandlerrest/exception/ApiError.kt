package com.halcyonmobile.errorhandlerrest.exception

/**
 * Error representing a failure response. It is thrown when the response is not 200 and the error body could be parsed.
 *
 * @property error The parsed error body (the one which is provided with [com.halcyonmobile.errorparsing.ParsedError] annotation) for the response.
 */
data class ApiError(val error: Any) : RemoteException(message = "The API responded with an error", cause = null)