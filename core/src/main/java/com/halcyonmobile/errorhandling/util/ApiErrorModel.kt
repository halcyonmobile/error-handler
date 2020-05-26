package com.halcyonmobile.errorhandling.util

import com.halcyonmobile.errorhandlerrest.util.HttpStatusCode
import com.halcyonmobile.errorhandling.error.ApiErrorCode
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

typealias Code = String

@JsonClass(generateAdapter = true)
data class ApiErrorModel(
    @Json(name = "status") val status: HttpStatusCode,
    @Json(name = "message") val message: String,
    @Json(name = "timestamp") val timestamp: String,
    // Unique identifier for this specific error
    @Json(name = "traceId") val traceId: String,
    @Json(name = "errors") val errors: List<Error>?
) {
    @JsonClass(generateAdapter = true)
    data class Error(
        @Json(name = "code") val code: Code,
        @Json(name = "errorMessage") val errorMessage: String,
        @Json(name = "developerMessage") val developerMessage: String,
        @Json(name = "field") val field: String?
    )
}

fun ApiErrorModel.causedBy(errorCode: ApiErrorCode) = errors?.map(ApiErrorModel.Error::code)?.contains(errorCode.value) ?: false