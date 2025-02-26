package su.femboymatrix.buttplug

import android.app.Application
import su.femboymatrix.buttplug.data.DefaultFemboyAppContainer
import su.femboymatrix.buttplug.data.FemboyAppContainer

class FemboyApplication : Application() {
    lateinit var container: FemboyAppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultFemboyAppContainer()
    }
}