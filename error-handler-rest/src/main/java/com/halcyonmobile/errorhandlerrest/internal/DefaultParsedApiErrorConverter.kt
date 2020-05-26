package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.exception.ApiError
import com.halcyonmobile.errorhandlerrest.exception.RemoteException

/**
 * Default [ParsedApiErrorConverter] which maps the parsed error into an [ApiError].
 */
internal class DefaultParsedApiErrorConverter : ParsedApiErrorConverter {

    override fun convert(parsedError: Any): RemoteException = ApiError(parsedError)
}