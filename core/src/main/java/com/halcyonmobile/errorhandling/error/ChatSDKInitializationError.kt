package com.halcyonmobile.errorhandling.error

import com.halcyonmobile.errorhandlercore.DataLayerException

/**
 * Other exceptions which can happen in the data layer. Domain specific exceptions/errors.
 */
class ChatSDKInitializationError : DataLayerException()
class FileNotFoundException : DataLayerException()