package dev.stock.dysnomia

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.OfflineRepository
import dev.stock.dysnomia.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant
import java.io.IOException
import javax.inject.Inject


@HiltAndroidApp
class DysnomiaApplication : Application() {
    @Inject
    lateinit var offlineRepository: OfflineRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var networkRepository: NetworkRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            plant(FirebaseReportingTree())
        }

        applicationScope.launch {
            offlineRepository.deletePendingMessages()

            val refreshToken = preferencesRepository.refreshToken.first()
            if (refreshToken.isEmpty()) {
                return@launch
            }

            try {
                preferencesRepository.saveTokens(
                    networkRepository.getNewTokens(
                        "Bearer $refreshToken"
                    )
                )
            } catch (e: HttpException) {
                if (e.code() in listOf(400, 401)) {
                    Timber.d(e, "Invalid refresh token, clearing account...")
                    preferencesRepository.clearAccount()
                } else {
                    Timber.d(e, "Unexpected HttpException while refreshing tokens, aborting...")
                }
            } catch (e: IOException) {
                Timber.d(e, "IOException while refreshing tokens, aborting...")
            }
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
