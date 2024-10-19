package ano.subcase.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ano.subcase.CaseStatus
import ano.subcase.server.BackendServer
import ano.subcase.server.FrontendServer
import ano.subcase.util.NotificationUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SubStoreService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val frontendPort = intent.getIntExtra("frontendPort", 8080)
        val backendPort = intent.getIntExtra("backendPort", 8081)

        val allowLan = intent.getBooleanExtra("allowLan", false)

        BackendServer(
            allowLan = true
        ).start()

        FrontendServer().start()

        NotificationUtil.startNotification(this)

        CaseStatus.isServiceRunning.value = true

        return START_STICKY
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()

        CaseStatus.isServiceRunning.value = false

//        NativeBridge.nativeStopBackend()
//        NativeBridge.nativeStopFrontend()

        NotificationUtil.stopNotification()
    }
}