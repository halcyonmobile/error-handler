package com.halcyonmobile.errorhandling.shared.error

import com.halcyonmobile.errorhandlercore.DataLayerException

/**
 * An application will definitely have some repetitive error cases, like no connection, authentication missing, and so on. For these cases, a general, base
 * implementation is suggested. This can be a class, like this one or a function or anything which works. Using this base implementation, every api call should
 * first check the specific errors, @see [com.halcyonmobile.errorhandling.feature.sample.SampleViewModel] and then leave the generic cases to be handled by this
 * base implementation.
 */
interface ErrorParser {

    fun extractErrorInfoFromException(dataLayerException: DataLayerException, retryAction: (() -> Unit)?, contactUsAction: (() -> Unit)?): ErrorItem
}