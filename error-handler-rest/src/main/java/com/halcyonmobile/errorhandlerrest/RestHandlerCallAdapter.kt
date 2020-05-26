package com.halcyonmobile.errorhandlerrest

import com.halcyonmobile.errorhandlerrest.internal.DefaultParsedApiErrorConverter
import com.halcyonmobile.errorhandlerrest.internal.NetworkExceptionToDataLayerExceptionConverter
import com.halcyonmobile.errorhandlerrest.logger.ErrorLogger
import com.halcyonmobile.errorhandlerrest.logger.NetworkErrorLogger
import com.halcyonmobile.errorparsing.ErrorWrappingAndParserCallAdapterFactory

object RestHandlerCallAdapter {

    /**
     * Builder for [ErrorWrappingAndParserCallAdapterFactory].
     */
    class Builder {
        private val listOfLoggers = mutableListOf<NetworkErrorLogger>()

        private var apiMapper: ParsedApiErrorConverter? = null
        private var serializationConverter: SerializationExceptionConverter? = null

        /**
         * Optional converter to return different error than [com.halcyonmobile.errorhandlerrest.exception.ApiError] if the Response contains an error body.
         *
         * @param converter [ParsedApiErrorConverter] Custom converter which returns a custom [com.halcyonmobile.errorhandlerrest.exception.RemoteException].
         */
        fun addParsedErrorConverter(converter: ParsedApiErrorConverter) = this.apply {
            apiMapper = converter
        }

        /**
         * Optional converter which adapts any Serialization related exception (depending what serialization library is used) to an internal
         * [com.halcyonmobile.errorhandlerrest.exception.SerializationException]. If it's not provided, by default a
         * [com.halcyonmobile.errorhandlerrest.exception.ServerCommunicationException] will be returned wrapping the original exception.
         */
        fun addSerializationConverter(converter: SerializationExceptionConverter) = this.apply {
            serializationConverter = converter
        }

        /**
         * Registers a new [NetworkErrorLogger].
         * It will log all classes implementing the [com.halcyonmobile.errorhandlerrest.logger.Loggable] interface.
         * If you have defined a custom exception for [com.halcyonmobile.errorhandlerrest.exception.RemoteException] and you want to log that as well,
         * make sure that your exception implements the [com.halcyonmobile.errorhandlerrest.logger.Loggable] interface.
         *
         * @param networkErrorLogger The new logger which will be added.
         */
        fun addNetworkErrorLogger(networkErrorLogger: NetworkErrorLogger) = this.also {
            listOfLoggers.add(networkErrorLogger)
        }

        /**
         * Returns the build [ErrorWrappingAndParserCallAdapterFactory] with the applied configurations.
         */
        fun build(): ErrorWrappingAndParserCallAdapterFactory =
            ErrorWrappingAndParserCallAdapterFactory(
                networkExceptionConverter = NetworkExceptionToDataLayerExceptionConverter(
                    apiErrorMapper = apiMapper ?: DefaultParsedApiErrorConverter(),
                    serializationConverter = serializationConverter
                ),
                workWithoutAnnotation = false,
                errorParsingFailureLogger = null
            ).also {
                // Only add the errors when we "build" the adapter.
                ErrorLogger.add(listOfLoggers)
            }
    }
}