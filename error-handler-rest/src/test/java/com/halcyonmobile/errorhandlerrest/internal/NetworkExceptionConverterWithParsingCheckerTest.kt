package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.SerializationExceptionConverter
import com.halcyonmobile.errorhandlerrest.exception.ParsingError
import com.halcyonmobile.errorhandlerrest.exception.SerializationException
import com.halcyonmobile.errorparsing.NetworkException
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test

class NetworkExceptionConverterWithParsingCheckerTest {

    private lateinit var sut: NetworkExceptionToDataLayerExceptionConverter
    private val apiErrorConverter: ParsedApiErrorConverter = mock()

    @Before
    fun setup() {
        sut = NetworkExceptionToDataLayerExceptionConverter(
            apiErrorMapper = apiErrorConverter,
            serializationConverter = MyTestParsingConverter()
        )
    }

    @Test(expected = ParsingError::class)
    fun `GIVEN provided custom serialization exception converter and a serialization exception is wrapped into NetworkException WHEN the conversion is made THEN a Parsing error is returned`() {

        val networkException = NetworkException(MyTestParsingException(), null, null)

        throw sut.convert(networkException)
    }

    private class MyTestParsingConverter : SerializationExceptionConverter {
        override fun convert(networkException: NetworkException): NetworkException =
            when (val cause = networkException.cause) {
                is MyTestParsingException -> SerializationException(cause)
                else -> networkException
            }
    }

    private class MyTestParsingException : RuntimeException()
}