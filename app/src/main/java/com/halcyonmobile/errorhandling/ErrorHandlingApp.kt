package com.halcyonmobile.errorhandling

import android.app.Application
import android.util.Log
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.halcyonmobile.errorhandling.core.di.provideCoreModules
import com.halcyonmobile.errorhandling.feature.featureModule
import com.pandulapeter.beagle.Beagle
import com.pandulapeter.beagleCore.configuration.Trick
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
            modules(appModule + featureModule + provideCoreModules("https://warm-tundra-40525.herokuapp.com/", BuildConfig.DEBUG))
        }
        Beagle.imprint(this)
        Beagle.learn(
            Trick.Header(
                title = getString(R.string.app_name),
                subtitle = packageName,
                text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            ),
            Trick.AppInfoButton(),
            Trick.ScreenshotButton(),
            Trick.KeylineOverlayToggle(),
            Trick.ViewBoundsOverlayToggle(),
            Trick.AnimatorDurationToggle(),
            Trick.DeviceInformationKeyValue()
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