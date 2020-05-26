package com.halcyonmobile.errorhandling.error

enum class ApiErrorCode(val value: String) {
    INVALID_CREDENTIALS("invalid_credentials"),
    MISSING_FIELD("missing_field"),
    USERNAME_TAKEN("username_taken")
}