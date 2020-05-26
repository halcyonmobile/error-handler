package com.halcyonmobile.errorhandlerrest.exception

import com.halcyonmobile.errorparsing.NetworkException

/**
 * Exception representing any kind of serialization failure.
 *
 * @see [com.halcyonmobile.errorhandlerrest.SerializationExceptionConverter]
 */
class SerializationException(throwable: Throwable) : NetworkException(throwable, null, null)