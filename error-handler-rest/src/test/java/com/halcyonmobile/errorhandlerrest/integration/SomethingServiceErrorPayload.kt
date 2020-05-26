package com.halcyonmobile.errorhandlerrest.integration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SomethingServiceErrorPayload(
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String
)