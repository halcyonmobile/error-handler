package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.exception.ApiError
import com.halcyonmobile.errorhandlerrest.exception.ErrorPayloadParsingException
import com.halcyonmobile.errorhandlerrest.exception.InternalServerError
import com.halcyonmobile.errorhandlerrest.exception.NoConnectionError
import com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException
import com.halcyonmobile.errorparsing.NetworkException
import com.halcyonmobile.errorparsing.NoNetworkException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NetworkExceptionConverterTest {

    private lateinit var sut: NetworkExceptionToDataLayerExceptionConverter

    private val apiErrorConverter: ParsedApiErrorConverter = mock()

    @Before
    fun setup() {
        sut = NetworkExceptionToDataLayerExceptionConverter(
            apiErrorConverter,
            null
        )
    }

    @Test(expected = NoConnectionError::class)
    fun `GIVEN a no connection network error WHEN conversion happens THEN a NoConnectionError is received`() {
        val networkException = NoNetworkException(null)

        throw sut.convert(networkException)
    }

    @Test
    fun `GIVEN a http exception with a correctly parsed payload WHEN it's converted THEN the parsed error is used`() {
        data class MyCustomException(val name: String)

        whenever(apiErrorConverter.convert(any())).doReturn(ApiError(MyCustomException("parsed test exception")))

        val networkException = NetworkException(
            throwable = HttpException(Response.error<String>(404, ResponseBody.create(null, ""))),
            parsedError = MyCustomException("test exception"),
            errorBody = ""
        )

        val convertedException = sut.convert(networkException)

        verify(apiErrorConverter, times(1)).convert(any())
        Assert.assertEquals((convertedException as ApiError).error, MyCustomException("parsed test exception"))
    }

    @Test(expected = InternalServerError::class)
    fun `GIVEN a http response and exception with internal server error status WHEN it's converted THEN the internal server error remote error is emitted`() {

        val networkException = NetworkException(
            throwable = HttpException(Response.error<String>(500, ResponseBody.create(null, ""))),
            parsedError = null,
            errorBody = ""
        )

        throw sut.convert(networkException)
    }

    @Test(expected = ErrorPayloadParsingException::class)
    fun `GIVEN a http error response but with failing payload parsing WHEN it's converted THEN a parsing exception error will be emitted`() {
        val networkException = NetworkException(
            throwable = HttpException(Response.error<String>(404, ResponseBody.create(null, ""))),
            parsedError = null,
            errorBody = ""
        )

        throw sut.convert(networkException)
    }

    @Test(expected = ServerCommunicationException::class)
    fun `GIVEN an I-O error while the HTTP call is executed WHEN it's converted THEN a remote fallback exception is emitted`() {
        val networkException = NetworkException(IOException(), null, null)

        throw sut.convert(networkException)
    }

    @Test(expected = ServerCommunicationException::class)
    fun `GIVEN an unexpected exception with the execution of the HTTP call WHEN it's converted THEN a remote fallback exception is emitted`() {
        val networkException = NetworkException(Exception("Something went wrong"), null, null)

        throw sut.convert(networkException)
    }
}