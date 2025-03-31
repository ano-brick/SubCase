package ano.subcase.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ano.subcase.CaseStatus
import ano.subcase.engine.CaseEngine
import ano.subcase.util.NotificationUtil

class SubStoreService : Service() {

    companion object {
        var caseEngine: CaseEngine? = null
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val frontendPort = intent.getIntExtra("frontendPort", 8080)
        val backendPort = intent.getIntExtra("backendPort", 8081)

        val allowLan = intent.getBooleanExtra("allowLan", false)

        caseEngine = CaseEngine(backendPort = backendPort, frontendPort = frontendPort, allowLan = allowLan)

        caseEngine!!.startServer()

        NotificationUtil.startNotification(this)

        CaseStatus.isServiceRunning.value = true

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        caseEngine!!.stopServer()
        CaseStatus.isServiceRunning.value = false
        NotificationUtil.stopNotification()
    }
}