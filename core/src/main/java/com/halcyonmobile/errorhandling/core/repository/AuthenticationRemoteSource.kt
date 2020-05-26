package com.halcyonmobile.errorhandling.core.repository

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandling.core.api.AuthenticationService
import com.halcyonmobile.errorhandling.core.api.LoginRequestBody
import com.halcyonmobile.errorhandling.core.api.RegisterRequestBody
import com.halcyonmobile.errorhandling.core.api.toModel

internal class AuthenticationRemoteSource(private val authenticationService: AuthenticationService) {

    @Throws(RemoteException::class)
    suspend fun login(userName: String, password: String) = authenticationService.login(LoginRequestBody(userName, password)).toModel()

    @Throws(RemoteException::class)
    suspend fun register(userName: String, firstName: String, lastName: String, email: String, password: String) =
        authenticationService.register(RegisterRequestBody(userName, firstName, lastName, email, password)).toModel()
}