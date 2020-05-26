package com.halcyonmobile.errorhandlerrest

import com.halcyonmobile.errorhandlerrest.exception.RemoteException

/**
 * A simple converter which offers the possibility of throwing an api related error with a typed error payload instead of [Any].
 */
interface ParsedApiErrorConverter {

    /**
     * @param parsedError [Any] Represents the error body parsed into an instance of an instance of the type passed through
     * [com.halcyonmobile.errorparsing.ParsedError].
     */
    fun convert(parsedError: Any): RemoteException
}