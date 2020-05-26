package com.halcyonmobile.errorhandlerrest.logger

import com.halcyonmobile.errorhandlerrest.exception.RemoteException

/**
 * Whenever a [RemoteException] happens which implements the [Loggable] interface, it will be logged by this.
 */
internal object ErrorLogger {

    private val ERROR_LOGGER_ARRAY: ArrayList<NetworkErrorLogger> = arrayListOf()
    private var loggersList: List<NetworkErrorLogger> = emptyList()

    internal fun log(remoteException: RemoteException) {
        if (remoteException is Loggable) {
            val listOfLoggers = loggersList
            for (logger in listOfLoggers) {
                logger.logError(remoteException)
            }
        }
    }

    fun add(list: List<NetworkErrorLogger>) = asSynchronized {
        ERROR_LOGGER_ARRAY.addAll(list)
        loggersList = ERROR_LOGGER_ARRAY.toList()
    }

    private inline fun <T> asSynchronized(crossinline func: () -> T) = synchronized(ERROR_LOGGER_ARRAY) {
        func()
    }
}