package com.halcyonmobile.errorhandlerrest.util

import com.halcyonmobile.errorhandlercore.ResultWrapper
import com.halcyonmobile.errorhandlerrest.exception.InternalServerError
import com.halcyonmobile.errorhandlerrest.exception.NoConnectionError
import com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun `GIVEN an internal server error remote exception WHEN wrapping it to result THEN it is propagated down as an exception`() = runBlocking {
        val testFunction: (suspend () -> Any) = mock()
        whenever(testFunction.invoke()).doThrow(InternalServerError())

        val wrappedResult = wrapToResult(testFunction)

        Assert.assertTrue(wrappedResult is ResultWrapper.Exception)
        Assert.assertTrue((wrappedResult as ResultWrapper.Exception).exception is InternalServerError)
    }

    @Test
    fun `GIVEN a retrofit I-O exception represented with server communication error WHEN wrapping it to result THEN it is propagated down as an exception`() = runBlocking {
        val testFunction: (suspend () -> Any) = mock()
        whenever(testFunction.invoke()).doThrow(ServerCommunicationException())

        val wrappedResult = wrapToResult(testFunction)

        Assert.assertTrue(wrappedResult is ResultWrapper.Exception)
        Assert.assertTrue((wrappedResult as ResultWrapper.Exception).exception is ServerCommunicationException)
    }

    @Test
    fun `GIVEN any other data layer exception WHEN wrapping it to result THEN it is propagated down as an expected error`() = runBlocking {
        val testFunction: (suspend () -> Any) = mock()
        whenever(testFunction.invoke()).doThrow(NoConnectionError())

        val wrappedResult = wrapToResult(testFunction)

        Assert.assertTrue(wrappedResult is ResultWrapper.Error)
        Assert.assertTrue((wrappedResult as ResultWrapper.Error).dataLayerException is NoConnectionError)
    }

    @Test
    fun `GIVEN any unexpected exception WHEN wrapping it to result THEN it is propagated down as an exception`() = runBlocking {
        val testFunction: (suspend () -> Any) = mock()
        whenever(testFunction.invoke()).doThrow(NullPointerException())

        val wrappedResult = wrapToResult(testFunction)

        Assert.assertTrue(wrappedResult is ResultWrapper.Exception)
    }
}