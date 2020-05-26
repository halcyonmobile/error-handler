package com.halcyonmobile.errorhandling.util

import com.halcyonmobile.errorhandlerrest.SerializationExceptionConverter
import com.halcyonmobile.errorhandlerrest.exception.SerializationException
import com.halcyonmobile.errorparsing.NetworkException
import com.squareup.moshi.JsonDataException

class DemoJsonDataExceptionToSerializationConverter : SerializationExceptionConverter {

    override fun convert(networkException: NetworkException): NetworkException {
        return with(networkException.cause) {
            if (this is JsonDataException) {
                SerializationException(this)
            } else {
                networkException
            }
        }
    }
}