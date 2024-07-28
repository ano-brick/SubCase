package ano.subcase.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ano.subcase.service.SubStoreService
import ano.subcase.caseApp
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
        intent.putExtra("backendPort", 8080)
        intent.putExtra("frontendPort", 8081)
        intent.putExtra("allowLan", allowLan)

        caseApp.startService(intent)
    }

    fun stopService() {
        caseApp.stopService(Intent(caseApp, SubStoreService::class.java))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkAndUpdate() {
        GlobalScope.launch {
            val lastestBackendVer = GithubUtil.getLatestVersion(REPO_BACKEND)
            val lastestFrontendVer = GithubUtil.getLatestVersion(REPO_FRONTEND)

            if (lastestFrontendVer != ConfigStore.frontendLocalVer) {
                withContext(Dispatchers.Main) {
                    Timber.d("Updating frontend to $lastestFrontendVer")
                    Toast.makeText(caseApp, "Updating frontend to $lastestFrontendVer", Toast.LENGTH_SHORT).show()
                }

                // Start download frontend
                val result = GithubUtil.downloadFile(
                    REPO_FRONTEND,
                    lastestFrontendVer,
                    "dist.zip",
                    caseApp.filesDir.absolutePath
                )

                if (result.isSuccess) {
                    val zipPath = caseApp.filesDir.path + "/dist.zip"
                    unzip(File(zipPath), File(caseApp.filesDir.path))

                    if (Files.exists(Paths.get(caseApp.filesDir.path + "/frontend"))) {
                        File(caseApp.filesDir.path + "/frontend").deleteRecursively()

                        Files.move(
                            Paths.get(caseApp.filesDir.path + "/dist"),
                            Paths.get(caseApp.filesDir.path + "/frontend")
                        )

                        ConfigStore.frontendLocalVer = lastestFrontendVer

                        val msg = "Frontend updated to $lastestFrontendVer"
                        Timber.d(msg)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(caseApp, "Frontend updated to $lastestFrontendVer", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                } else {
                    Timber.w("Failed to download frontend")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(caseApp, "Failed to download frontend", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (lastestBackendVer != ConfigStore.backendLocalVer) {
                withContext(Dispatchers.Main) {
                    Timber.d("Updating backend to $lastestBackendVer")
                    Toast.makeText(caseApp, "Updating backend to $lastestBackendVer", Toast.LENGTH_SHORT).show()
                }

                // Start download backend
                val script0Result = GithubUtil.downloadFile(
                    REPO_BACKEND,
                    lastestBackendVer,
                    "sub-store-0.min.js",
                    caseApp.filesDir.absolutePath
                )

                val script1Result = GithubUtil.downloadFile(
                    REPO_BACKEND,
                    lastestBackendVer,
                    "sub-store-1.min.js",
                    caseApp.filesDir.absolutePath
                )

                if (script0Result.isSuccess && script1Result.isSuccess) {
                    Files.deleteIfExists(Paths.get(caseApp.filesDir.path + "/backend/sub-store-0.min.js"))
                    Files.deleteIfExists(Paths.get(caseApp.filesDir.path + "/backend/sub-store-1.min.js"))

                    Files.move(
                        Paths.get(caseApp.filesDir.path + "/sub-store-0.min.js"),
                        Paths.get(caseApp.filesDir.path + "/backend/sub-store-0.min.js")
                    )

                    Files.move(
                        Paths.get(caseApp.filesDir.path + "/sub-store-1.min.js"),
                        Paths.get(caseApp.filesDir.path + "/backend/sub-store-1.min.js")
                    )

                    ConfigStore.backendLocalVer = lastestBackendVer

                    val msg = "Backend updated to $lastestBackendVer"
                    Timber.d(msg)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(caseApp, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Timber.w("Failed to download backend")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(caseApp, "Failed to download backend", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}