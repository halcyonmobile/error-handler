package com.halcyonmobile.errorhandlercore

/**
 * Wrapper for data layer operations with 3 options with clear separation between expected and unexpected errors.
 */
sealed class ResultWrapper<out T> {

    /**
     * Option meant for cases when everything went fine.
     *
     * @property data Holding the result.
     */
    data class Success<out T>(val data: T) : ResultWrapper<T>()

    /**
     * Option meant for expected errors which can occur in the data layer.
     * Expected errors are the ones which can happen even if everything seems fine, ex. an API returns a validation, permission, ... error, or the required
     * data can't be found (removed, moved, ...).
     *
     * @property dataLayerException Holds the exact error which occurred.
     */
    data class Error<out T>(val dataLayerException: DataLayerException) : ResultWrapper<T>()

    /**
     * Option meant for anything else which can't be put in the [Error], usually representing developers fault.
     * Ex. NullPointerException or permission exception and so on.
     *
     * @property exception Throwable describing the exact failure.
     */
    data class Exception<out T>(val exception: Throwable) : ResultWrapper<T>()

    /**
     * Determines whether the [ResultWrapper] was successful or not.
     *
     * @return true if it is [Success], false otherwise
     */
    fun isSuccess(): Boolean = this is Success<*>

    /**
     * Determines whether the [ResultWrapper] was one of the failures or not.
     *
     * @return true if it is [Error] or [Exception], false otherwise.
     */
    fun isFailure(): Boolean = !isSuccess()
}