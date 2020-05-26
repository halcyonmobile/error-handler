package com.halcyonmobile.errorhandling.shared.error

import androidx.annotation.StringRes

/**
 * Model class representing an error from ViewModel to View used to populate a SnackBar.
 *
 * It can have any structure based on the project needs. A similar structure can be made for full screen error placeholders (ex. when loading a list, with
 * fields like illustration, title, description, retry button, ...)
 */
data class ErrorItem(
    @StringRes val messageRes: Int,
    val traceId: String? = null,
    @StringRes val actionStringRes: Int? = null,
    val action: (() -> Unit)? = null,
    val showIndefinite: Boolean = false
)