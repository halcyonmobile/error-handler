package com.halcyonmobile.errorhandling

import com.halcyonmobile.errorhandlerrest.logger.NetworkErrorLogger
import com.halcyonmobile.errorhandling.shared.error.DemoErrorLogger
import com.halcyonmobile.errorhandling.shared.error.ErrorParser
import com.halcyonmobile.errorhandling.shared.error.ErrorParserBaseImpl
import org.koin.dsl.module

val appModule = module {

    factory<ErrorParser> { ErrorParserBaseImpl() }
    factory<NetworkErrorLogger> { DemoErrorLogger() }
}