package ano.subcase.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ano.subcase.caseApp
import ano.subcase.service.SubStoreService
import ano.subcase.util.AppUtil.unzip
import ano.subcase.util.ConfigStore
import ano.subcase.util.GithubUtil
import ano.subcase.util.REPO_BACKEND
import ano.subcase.util.REPO_FRONTEND
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class MainViewModel : ViewModel() {

    var allowLan by mutableStateOf(false)

    init {
        if (ConfigStore.isAllowLan) {
            allowLan = true
        }

        if (ConfigStore.isServiceRunning) {
            startService()
        }
    }

    fun startService() {
        val intent = Intent(caseApp, SubStoreService::class.java)
        intent.putExtra("backendPort", 8081)
        intent.putExtra("frontendPort", 8080)
        intent.putExtra("allowLan", allowLan)
        viewModelScope.launch {
            caseApp.startService(intent)
        }
    }

    fun stopService() {
        viewModelScope.launch {
            caseApp.stopService(Intent(caseApp, SubStoreService::class.java))
        }
    }
}