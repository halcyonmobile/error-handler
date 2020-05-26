package com.halcyonmobile.errorhandling.core.repository

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandling.core.api.UserService
import com.halcyonmobile.errorhandling.core.api.toModel

internal class UserRemoteSource(private val userService: UserService) {

    @Throws(RemoteException::class)
    suspend fun getUsers() = userService.getUsers().map { it.toModel() }
}