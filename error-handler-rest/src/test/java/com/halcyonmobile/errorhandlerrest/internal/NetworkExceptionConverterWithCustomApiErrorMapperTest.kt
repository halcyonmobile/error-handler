package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorparsing.NetworkException
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class NetworkExceptionConverterWithCustomApiErrorMapperTest {

    private lateinit var sut: NetworkExceptionToDataLayerExceptionConverter

    @Before
    fun setup() {
        sut = NetworkExceptionToDataLayerExceptionConverter(MyCustomMapper(), null)
    }

    @Test
    fun `GIVEN a custom error mapper for a correctly parsed error payload WHEN the response is converted THEN the custom api error mapper result will be emitted `() {

        val networkException = NetworkException(
            throwable = HttpException(Response.error<String>(404, ResponseBody.create(null, ""))),
            parsedError = "test error payload",
            errorBody = ""
        )

        val convertedException = sut.convert(networkException)

        Assert.assertEquals(MyCustomApiError("test error payload"), convertedException)
    }

    private class MyCustomMapper : ParsedApiErrorConverter {

        override fun convert(parsedError: Any): RemoteException = MyCustomApiError(parsedError as String)
    }

    private data class MyCustomApiError(val error: String) : RemoteException()
}