package com.halcyonmobile.errorhandling.feature

import com.halcyonmobile.errorhandling.feature.sample.SampleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel { SampleViewModel(register = get(), login = get(), getUsers = get(), errorParser = get()) }
}