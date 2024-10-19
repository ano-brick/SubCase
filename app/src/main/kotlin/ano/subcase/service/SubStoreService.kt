package ano.subcase.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ano.subcase.CaseStatus
import ano.subcase.server.BackendServer
import ano.subcase.server.FrontendServer
import ano.subcase.util.NotificationUtil

class SubStoreService : Service() {
    lateinit var backendServer: BackendServer
    lateinit var frontendServer: FrontendServer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val frontendPort = intent.getIntExtra("frontendPort", 8081)
        val backendPort = intent.getIntExtra("backendPort", 8080)

        val allowLan = intent.getBooleanExtra("allowLan", false)

        backendServer = BackendServer(
            port = backendPort,
            allowLan = allowLan
        )
        backendServer.start()

        frontendServer = FrontendServer(
            port = frontendPort,
            allowLan = allowLan
        )
        frontendServer.start()

        NotificationUtil.startNotification(this)

        CaseStatus.isServiceRunning.value = true

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        CaseStatus.isServiceRunning.value = false

        backendServer.stop()
        frontendServer.stop()

        NotificationUtil.stopNotification()
    }
}