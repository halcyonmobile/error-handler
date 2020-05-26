package com.halcyonmobile.errorhandlerrest.util

import com.halcyonmobile.errorhandlercore.DataLayerException
import com.halcyonmobile.errorhandlercore.ResultWrapper
import com.halcyonmobile.errorhandlerrest.exception.InternalServerError
import com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException

/**
 * Helper function which wraps the result of [func] into [ResultWrapper].
 * [InternalServerError] and [ServerCommunicationException] are redirected to [ResultWrapper.Exception] since they represent unexpected errors, while
 * the rest of [DataLayerException] are wrapped into [ResultWrapper.Error].
 * Since a data layer exception shouldn't crash the application, all other kind of [Throwable]s are catched and wrapped into [ResultWrapper.Exception].
 */
@Suppress("TooGenericExceptionCaught") // Done with the purpose that the core should never crash the app. The app should recover from it as he can.
suspend inline fun <T> wrapToResult(crossinline func: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(func())
    } catch (internalServerError: InternalServerError) {
        ResultWrapper.Exception(internalServerError)
    } catch (remoteException: ServerCommunicationException) {
        ResultWrapper.Exception(remoteException)
    } catch (dataLayerException: DataLayerException) {
        ResultWrapper.Error(dataLayerException)
    } catch (unhandledException: Throwable) {
        ResultWrapper.Exception(unhandledException)
    }
}