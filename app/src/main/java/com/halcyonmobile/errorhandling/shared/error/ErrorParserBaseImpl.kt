package com.halcyonmobile.errorhandling.shared.error

import com.halcyonmobile.errorhandlercore.DataLayerException
import com.halcyonmobile.errorhandlerrest.HttpStatus
import com.halcyonmobile.errorhandlerrest.exception.ConnectionTimedOutError
import com.halcyonmobile.errorhandlerrest.exception.ErrorPayloadParsingException
import com.halcyonmobile.errorhandlerrest.exception.InternalServerError
import com.halcyonmobile.errorhandlerrest.exception.NoConnectionError
import com.halcyonmobile.errorhandlerrest.exception.ParsingError
import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.util.HttpStatusCode
import com.halcyonmobile.errorhandling.R
import com.halcyonmobile.errorhandling.error.DemoAppApiError

open class ErrorParserBaseImpl : ErrorParser {

    override fun extractErrorInfoFromException(dataLayerException: DataLayerException, retryAction: (() -> Unit)?, contactUsAction: (() -> Unit)?): ErrorItem =
        when (dataLayerException) {
            is RemoteException -> parseRemoteException(dataLayerException, retryAction, contactUsAction)
            // Handle other DataLayerException variants as well if the app has any.
            else -> getSomethingWentWrongErrorItem()
        }

    private fun parseRemoteException(remoteException: RemoteException, retryAction: (() -> Unit)?, contactUsAction: (() -> Unit)?): ErrorItem =
        when (remoteException) {
            is DemoAppApiError -> {
                // Check for generic error codes returned from the backend

                // else
                getSomethingWentWrongErrorItem(remoteException.payload.traceId)
            }
            is ErrorPayloadParsingException -> getErrorItemFroStatusCode(remoteException.statusCode)
            is NoConnectionError -> ErrorItem(
                messageRes = R.string.no_connection,
                traceId = null,
                actionStringRes = R.string.try_again,
                action = retryAction,
                showIndefinite = true
            )
            is ConnectionTimedOutError -> ErrorItem(
                messageRes = R.string.error_timeout,
                traceId = null,
                actionStringRes = R.string.try_again,
                action = retryAction,
                showIndefinite = true
            )
            is ParsingError -> ErrorItem(
                messageRes = R.string.error_parsing_short,
                traceId = null,
                actionStringRes = R.string.contact_us,
                action = contactUsAction,
                showIndefinite = true
            )
            is InternalServerError -> ErrorItem(
                messageRes = R.string.error_internal_server_error_short,
                traceId = null,
                actionStringRes = R.string.contact_us,
                action = contactUsAction,
                showIndefinite = true
            )
            else -> getSomethingWentWrongErrorItem()
        }

    private fun getErrorItemFroStatusCode(statusCode: HttpStatusCode) =
        when (statusCode) {
            HttpStatus.FORBIDDEN -> ErrorItem(
                messageRes = R.string.error_generic_forbidden,
                traceId = null,
                actionStringRes = null,
                action = null
            )
            HttpStatus.UNAUTHORIZED -> ErrorItem(
                messageRes = R.string.error_generic_unauthorized,
                traceId = null,
                actionStringRes = null,
                action = null
            )
            HttpStatus.NOT_FOUND -> ErrorItem(
                messageRes = R.string.error_generic_not_found,
                traceId = null,
                actionStringRes = null,
                action = null
            )
            else -> getSomethingWentWrongErrorItem()
        }

    private fun getSomethingWentWrongErrorItem(traceId: String? = null) = ErrorItem(
        messageRes = R.string.error_something_went_wrong,
        traceId = traceId,
        actionStringRes = null,
        action = null
    )
}