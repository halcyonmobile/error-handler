package com.halcyonmobile.errorhandling.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class LoginRequestBody(@Json(name = "username") val userName: String, @Json(name = "password") val password: String)