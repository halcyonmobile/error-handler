package com.halcyonmobile.errorhandling.core.api

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandling.core.dto.UserDto
import com.halcyonmobile.errorhandling.util.ApiErrorModel
import com.halcyonmobile.errorparsing.ParsedError
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthenticationService {

    @ParsedError(ApiErrorModel::class)
    @Throws(RemoteException::class)
    @POST("auth/login")
    suspend fun login(@Body loginBody: LoginRequestBody): UserDto

    @ParsedError(ApiErrorModel::class)
    @Throws(RemoteException::class)
    @POST("auth/register")
    suspend fun register(@Body registerBody: RegisterRequestBody): UserDto
}