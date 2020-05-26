package com.halcyonmobile.errorhandling.core.usecase

import com.halcyonmobile.errorhandlerrest.util.wrapToResult
import com.halcyonmobile.errorhandling.core.repository.UserRemoteSource

class GetUsersUseCase internal constructor(private val userRemoteSource: UserRemoteSource) {

    suspend operator fun invoke() = wrapToResult {
        userRemoteSource.getUsers()
    }
}