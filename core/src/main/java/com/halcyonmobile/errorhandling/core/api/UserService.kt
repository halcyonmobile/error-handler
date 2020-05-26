package com.halcyonmobile.errorhandling.core.api

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandling.core.dto.UserDto
import com.halcyonmobile.errorhandling.util.ApiErrorModel
import com.halcyonmobile.errorparsing.ParsedError
import retrofit2.http.GET

internal interface UserService {
    @GET("users")
    @ParsedError(ApiErrorModel::class)
    @Throws(RemoteException::class)
    suspend fun getUsers(): List<UserDto>
}