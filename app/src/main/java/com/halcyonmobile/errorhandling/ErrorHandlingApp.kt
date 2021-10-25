package com.halcyonmobile.errorhandling

import android.app.Application
import android.util.Log
import com.halcyonmobile.errorhandling.core.di.provideCoreModules
import com.halcyonmobile.errorhandling.feature.featureModule
import com.pandulapeter.beagle.Beagle
import com.pandulapeter.beagle.common.configuration.Behavior
import com.pandulapeter.beagle.logOkHttp.BeagleOkHttpLogger
import com.pandulapeter.beagle.modules.AnimationDurationSwitchModule
import com.pandulapeter.beagle.modules.AppInfoButtonModule
import com.pandulapeter.beagle.modules.BugReportButtonModule
import com.pandulapeter.beagle.modules.DeveloperOptionsButtonModule
import com.pandulapeter.beagle.modules.DeviceInfoModule
import com.pandulapeter.beagle.modules.DividerModule
import com.pandulapeter.beagle.modules.ForceCrashButtonModule
import com.pandulapeter.beagle.modules.HeaderModule
import com.pandulapeter.beagle.modules.KeylineOverlaySwitchModule
import com.pandulapeter.beagle.modules.LifecycleLogListModule
import com.pandulapeter.beagle.modules.LogListModule
import com.pandulapeter.beagle.modules.NetworkLogListModule
import com.pandulapeter.beagle.modules.PaddingModule
import com.pandulapeter.beagle.modules.ScreenCaptureToolboxModule
import com.pandulapeter.beagle.modules.TextModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("unused")
class ErrorHandlingApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ErrorHandlingApp)
            modules(appModule + featureModule + provideCoreModules(BuildConfig.BASE_URL, BuildConfig.DEBUG))
        }

        Beagle.initialize(
            application = this,
            behavior = Behavior(
                networkLogBehavior = Behavior.NetworkLogBehavior(
                    baseUrl = BuildConfig.BASE_URL, // Just to reduce duplicated information in log previews
                    networkLoggers = listOf(BeagleOkHttpLogger) // or listOf(BeagleKtorLogger)
                )
            )
        )

        Beagle.set(
            HeaderModule(
                title = getString(R.string.app_name),
                subtitle = BuildConfig.APPLICATION_ID,
                text = "${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            ),
            AppInfoButtonModule(),
            DeveloperOptionsButtonModule(),
            PaddingModule(),
            TextModule("General", TextModule.Type.SECTION_HEADER),
            KeylineOverlaySwitchModule(),
            AnimationDurationSwitchModule(),
            ScreenCaptureToolboxModule(),
            DividerModule(),
            TextModule("Logs", TextModule.Type.SECTION_HEADER),
            NetworkLogListModule(),
            LogListModule(), // Use Beagle.log() or BeagleLogger.log() to push messages
            LifecycleLogListModule(),
            DividerModule(),
            TextModule("Other", TextModule.Type.SECTION_HEADER),
            DeviceInfoModule(),
            BugReportButtonModule(),
            ForceCrashButtonModule()
        )

        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
    }

    /**
     * Timber Tree which sends error logs to Firebase Crashlytics
     */
    private class CrashReportingTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Filter out non-error log messages
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
//            FirebaseCrashlytics.getInstance().log(message)
            if (t != null) {
//                FirebaseCrashlytics.getInstance().recordException(t)
            }
        }
    }
}