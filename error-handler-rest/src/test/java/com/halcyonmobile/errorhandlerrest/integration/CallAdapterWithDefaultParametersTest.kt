package com.halcyonmobile.errorhandlerrest.integration

import com.halcyonmobile.errorhandlerrest.RestHandlerCallAdapter
import com.halcyonmobile.errorhandlerrest.exception.ApiError
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

class CallAdapterWithDefaultParametersTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: SomethingService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .addCallAdapterFactory(RestHandlerCallAdapter.Builder().build())
            .baseUrl(mockWebServer.url("test-url/").toString())
            .build()
            .create(SomethingService::class.java)
    }

    @After
    fun shutDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN default parameters for call adapter and a valid error response is received WHEN the request is executed THEN the error is wrapped into a remote exception`() =
        runBlocking {

            val expectedException = ApiError(SomethingServiceErrorPayload("something_went_wrong", "Api call failed"))
            val testResponse = MockResponse().setResponseCode(404).setBody(readJsonResourceFileToString("mocks/service_error_response.json"))
            mockWebServer.enqueue(testResponse)

            try {
                service.getSomething()
            } catch (ex: RemoteException) {
                Assert.assertTrue(ex is ApiError)
                Assert.assertEquals(expectedException, ex)
                return@runBlocking
            }

            Assert.assertTrue("The request didn't throw the expected exception", false)
        }
}