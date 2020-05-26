package com.halcyonmobile.errorhandlerrest.integration

import com.halcyonmobile.errorhandlerrest.ParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.RestHandlerCallAdapter
import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.readJsonResourceFileToString
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CallAdapterWithApiErrorMapperTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: SomethingService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .addCallAdapterFactory(RestHandlerCallAdapter.Builder().addParsedErrorConverter(MyCustomApiErrorMapper()).build())
            .baseUrl(mockWebServer.url("test-url/").toString())
            .build()
            .create(SomethingService::class.java)
    }

    @After
    fun shutDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN a custom api error mapper and a valid api error response WHEN the request is executed THEN the error is the output of the passed api error mapper`() =
        runBlocking {

            val expectedException = SomethingServiceErrorPayload("something_went_wrong", "Api call failed")
            val testResponse = MockResponse().setResponseCode(404).setBody(readJsonResourceFileToString("mocks/service_error_response.json"))
            mockWebServer.enqueue(testResponse)

            try {
                service.getSomething()
            } catch (ex: RemoteException) {
                Assert.assertTrue(ex is MyCustomApiException)
                Assert.assertEquals(MyCustomApiException(error = expectedException), ex)
                return@runBlocking
            }

            Assert.assertTrue("The request didn't thrown the expected exception", false)
        }

    private class MyCustomApiErrorMapper : ParsedApiErrorConverter {

        override fun convert(parsedError: Any): RemoteException =
            if (parsedError is SomethingServiceErrorPayload) {
                MyCustomApiException(parsedError)
            } else {
                RemoteException("Parsing failed, shouldn't end up in this situation")
            }
    }

    private data class MyCustomApiException(val error: SomethingServiceErrorPayload) : RemoteException()
}