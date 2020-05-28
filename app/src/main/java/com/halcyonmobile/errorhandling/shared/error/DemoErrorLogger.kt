package com.halcyonmobile.errorhandling.shared.error

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandlerrest.logger.NetworkErrorLogger
import timber.log.Timber

/**
 * A sample to error logging.
 */
class DemoErrorLogger : NetworkErrorLogger {

    override fun logError(remoteException: RemoteException) {
        Timber.e(remoteException)
    }
}