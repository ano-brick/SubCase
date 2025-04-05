package ano.subcase.service

import android.content.Intent
import ano.subcase.CaseStatus
import ano.subcase.caseApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ServiceManager {

    fun startService() {
        val intent = Intent(caseApp, SubStoreService::class.java)
        intent.putExtra("backendPort", 8081)
        intent.putExtra("frontendPort", 8080)
        intent.putExtra("allowLan", CaseStatus.isAllowLan.value)
        GlobalScope.launch {
            caseApp.startService(intent)
        }
    }

    fun stopService() {
        GlobalScope.launch {
            caseApp.stopService(Intent(caseApp, SubStoreService::class.java))
        }
    }
}