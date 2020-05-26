package com.halcyonmobile.errorhandling.core.usecase

import com.halcyonmobile.errorhandlerrest.util.wrapToResult
import com.halcyonmobile.errorhandling.core.repository.AuthenticationRemoteSource

class RegisterUseCase internal constructor(private val authenticationRemoteSource: AuthenticationRemoteSource) {

    suspend operator fun invoke(
        userName: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) = wrapToResult {
        authenticationRemoteSource.register(userName, firstName, lastName, email, password)
    }
}