package com.halcyonmobile.errorhandling.core.di

import com.halcyonmobile.errorhandlerrest.RestHandlerCallAdapter
import com.halcyonmobile.errorhandling.core.api.AuthenticationService
import com.halcyonmobile.errorhandling.core.api.UserService
import com.halcyonmobile.errorhandling.core.repository.AuthenticationRemoteSource
import com.halcyonmobile.errorhandling.core.repository.UserRemoteSource
import com.halcyonmobile.errorhandling.core.usecase.GetUsersUseCase
import com.halcyonmobile.errorhandling.core.usecase.LoginUseCase
import com.halcyonmobile.errorhandling.core.usecase.RegisterUseCase
import com.halcyonmobile.errorhandling.error.DemoAppApiErrorConverter
import com.halcyonmobile.errorhandling.error.DemoErrorLogger
import com.halcyonmobile.errorhandling.util.DemoJsonDataExceptionToSerializationConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideCoreModules(baseUrl: String, enableHttpLogging: Boolean) =
    provideNetworkingModule(baseUrl, enableHttpLogging) + provideRemoteSourceModule() + provideUseCaseModule()

internal fun provideNetworkingModule(baseUrl: String, enableHttpLogging: Boolean) = module {
    single {
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = if (enableHttpLogging) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }).build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(
                RestHandlerCallAdapter.Builder()
                    .addParsedErrorConverter(DemoAppApiErrorConverter())
                    .addSerializationConverter(DemoJsonDataExceptionToSerializationConverter())
                    .addNetworkErrorLogger(DemoErrorLogger())
                    .build()
            )
            .build()
    }
    factory { get<Retrofit>().create(AuthenticationService::class.java) }
    factory { get<Retrofit>().create(UserService::class.java) }
}

internal fun provideRemoteSourceModule() = module {
    factory { AuthenticationRemoteSource(authenticationService = get()) }
    factory { UserRemoteSource(userService = get()) }
}

internal fun provideUseCaseModule() = module {
    factory { LoginUseCase(authenticationRemoteSource = get()) }
    factory { RegisterUseCase(authenticationRemoteSource = get()) }
    factory { GetUsersUseCase(userRemoteSource = get()) }
}