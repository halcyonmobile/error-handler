package com.halcyonmobile.errorhandlerrest

import com.halcyonmobile.errorparsing.NetworkException

/**
 * A converter class that converts the different types of parsing exceptions of json parsing libraries
 * (such as Moshi - JsonDataException, Gson - JsonParseException, etc.)
 * to a general [com.halcyonmobile.errorhandlerrest.exception.SerializationException]
 */
interface SerializationExceptionConverter {

    fun convert(networkException: NetworkException): NetworkException
}