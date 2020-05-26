package com.halcyonmobile.errorhandlerrest.integration

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorparsing.ParsedError
import retrofit2.http.GET

interface SomethingService {

    @Throws(RemoteException::class)
    @ParsedError(SomethingServiceErrorPayload::class)
    @GET("/something")
    suspend fun getSomething(): String
}