package com.halcyonmobile.errorhandling.core.usecase

import com.halcyonmobile.errorhandlerrest.util.wrapToResult
import com.halcyonmobile.errorhandling.core.repository.AuthenticationRemoteSource

class LoginUseCase internal constructor(private val authenticationRemoteSource: AuthenticationRemoteSource) {

    suspend operator fun invoke(userName: String, password: String) = wrapToResult {
        authenticationRemoteSource.login(userName, password)
    }
}