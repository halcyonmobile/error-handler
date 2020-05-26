package com.halcyonmobile.errorhandling.error

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.logger.NetworkErrorLogger

/**
 * A sample to error logging.
 */
class DemoErrorLogger : NetworkErrorLogger {

    override fun logError(remoteException: RemoteException) {
        val tag = "[NetworkWrapper/Logger]${remoteException.javaClass.name}"
        val message = if (remoteException.message.isNullOrBlank()) "[No Message]" else remoteException.message
        val cause = if (remoteException.cause == null) "[No Cause]" else remoteException.cause.toString()
        println(
            """
            $tag $message
            $cause
            """.trimIndent()
        )
    }
}