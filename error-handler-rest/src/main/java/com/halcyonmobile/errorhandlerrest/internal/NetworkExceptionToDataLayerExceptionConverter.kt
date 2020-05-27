package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.HttpStatus
import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.SerializationExceptionConverter
import com.halcyonmobile.errorhandlerrest.exception.ConnectionTimedOutError
import com.halcyonmobile.errorhandlerrest.exception.ErrorPayloadParsingException
import com.halcyonmobile.errorhandlerrest.exception.InternalServerError
import com.halcyonmobile.errorhandlerrest.exception.NoConnectionError
import com.halcyonmobile.errorhandlerrest.exception.ParsingError
import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.exception.SerializationException
import com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException
import com.halcyonmobile.errorhandlerrest.logger.ErrorLogger
import com.halcyonmobile.errorparsing.NetworkException
import com.halcyonmobile.errorparsing.NetworkExceptionConverter
import com.halcyonmobile.errorparsing.NoNetworkException
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * Converter class where [NetworkException] is translated into [RemoteException].
 *
 * @property apiErrorMapper Optional converter which offers the possibility to convert the parsedError from [NetworkException] when it's not null to a custom
 * one defined by the consumer. This conversion is useful when you want to work with a non [Any] payload and you can define the cast in a single place, namely
 * in this mapper. By default it will use a simple mapper, [DefaultParsedApiErrorConverter], which will return an error class which contains the payload as any.
 */
internal class NetworkExceptionToDataLayerExceptionConverter(
    private val apiErrorMapper: ParsedApiErrorConverter,
    private val serializationConverter: SerializationExceptionConverter?
) : NetworkExceptionConverter {

    override fun convert(networkException: NetworkException): RemoteException {

        return when (serializationConverter?.convert(networkException) ?: networkException) {
            is SerializationException -> ParsingError(networkException.message.toString(), networkException.cause)
            is NoNetworkException -> NoConnectionError(networkException.cause)
            else -> {
                when (val cause = networkException.cause) {
                    is SocketTimeoutException -> ConnectionTimedOutError(cause = cause)
                    is HttpException -> handleHttpException(networkException.parsedError, cause)
                    else -> ServerCommunicationException(cause?.message, cause)
                }
            }
        }.also {
            // By logging it here, no exception would be missed.
            ErrorLogger.log(it)
        }
    }

    private fun handleHttpException(parsedError: Any?, httpException: HttpException): RemoteException {
        return if (parsedError != null) {
            apiErrorMapper.convert(parsedError)
        } else {
            if (httpException.code() == HttpStatus.INTERNAL_SERVER_ERROR) {
                InternalServerError(httpException.message(), cause = httpException)
            } else {
                ErrorPayloadParsingException(httpException.code(), httpException)
            }
        }
    }
}