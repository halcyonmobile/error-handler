package com.halcyonmobile.errorhandling

import com.halcyonmobile.errorhandling.shared.error.ErrorParserBaseImpl
import com.halcyonmobile.errorhandling.shared.error.ErrorParser
import org.koin.dsl.module

val appModule = module {

    factory<ErrorParser> { ErrorParserBaseImpl() }
}