package dev.stock.dysnomia

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class DysnomiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            plant(FirebaseReportingTree())
        }
    }
}

class FirebaseReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null && (priority == Log.ERROR || priority == Log.WARN)) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
