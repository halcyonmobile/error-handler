package com.halcyonmobile.errorhandling.error

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException
import com.halcyonmobile.errorhandling.util.ApiErrorModel

class DemoAppApiErrorConverter : ParsedApiErrorConverter {

    override fun convert(parsedError: Any): RemoteException {
        return if (parsedError is ApiErrorModel) {
            DemoAppApiError(parsedError)
        } else {
            ServerCommunicationException()
        }
    }
}