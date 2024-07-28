package ano.subcase

import android.app.Application
import ano.subcase.util.AppUtil
import ano.subcase.util.ConfigStore
import timber.log.Timber

lateinit var caseApp: CaseApplication

class CaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Timber.d("TimberInitializer is initialized.")
        caseApp = this

        if (ConfigStore.isFirstOpen) {
            AppUtil.initFirstOpen()
            ConfigStore.isFirstOpen = false
        }
    }

}