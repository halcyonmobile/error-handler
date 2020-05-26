package com.halcyonmobile.errorhandling.core.api

import com.halcyonmobile.errorhandling.core.dto.UserDto
import com.halcyonmobile.errorhandling.core.model.User

fun UserDto.toModel() = User(id, userName, firstName, lastName, email)