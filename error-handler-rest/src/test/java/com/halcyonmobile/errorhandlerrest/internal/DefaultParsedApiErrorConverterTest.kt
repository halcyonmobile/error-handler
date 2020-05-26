package com.halcyonmobile.errorhandlerrest.internal

import com.halcyonmobile.errorhandlerrest.exception.ApiError
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class DefaultParsedApiErrorConverterTest {

    private lateinit var sut: DefaultParsedApiErrorConverter

    @Before
    fun setup() {
        sut = DefaultParsedApiErrorConverter()
    }

    @Test
    fun `GIVEN an arbitrary payload WHEN it's converted THEN it's simply wrapped into an ApiError`() {

        val payload = "TestPayload"

        val convertedError = sut.convert(payload)

        Assert.assertEquals(ApiError(payload), convertedError)
    }
}