package com.halcyonmobile.errorhandlerrest.logger

import com.halcyonmobile.errorhandlerrest.exception.RemoteException

interface NetworkErrorLogger {
    fun logError(remoteException: RemoteException)
}