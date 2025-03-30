package ano.subcase.util

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import ano.subcase.CaseStatus
import ano.subcase.caseApp
import ano.subcase.util.AppUtil.unzip
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object SubStore {

    val basePath = caseApp.filesDir

    var localFrontendVersion: String
        get() = ConfigStore.localFrontendVersion
        set(value) {
            ConfigStore.localFrontendVersion = value
        }

    var localBackendVersion: String
        get() = ConfigStore.localBackendVersion
        set(value) {
            ConfigStore.localBackendVersion = value
        }

    var remoteFrontendVersion = mutableStateOf(ConfigStore.localFrontendVersion)
    var remoteBackendVersion = mutableStateOf(ConfigStore.localBackendVersion)

    @OptIn(DelicateCoroutinesApi::class)
    fun checkLatestVersion() {
        GlobalScope.launch {
            remoteBackendVersion.value = GithubUtil.getLatestVersion(REPO_BACKEND)
            remoteFrontendVersion.value = GithubUtil.getLatestVersion(REPO_FRONTEND)

            if (remoteFrontendVersion.value != localFrontendVersion || remoteBackendVersion.value != localBackendVersion) {
                CaseStatus.showUpdateDialog.value = true
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateFrontend(): Result<Unit> {
        withContext(Dispatchers.Main) {
            Timber.d("Updating frontend to ${remoteFrontendVersion.value}")
            Toast.makeText(
                caseApp,
                "正在将前端更新到 v${remoteFrontendVersion.value}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Start download frontend
        val result = GithubUtil.downloadFile(
            REPO_FRONTEND,
            remoteFrontendVersion.value,
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

                ConfigStore.localFrontendVersion = remoteFrontendVersion.value

                val msg = "Frontend updated to ${remoteFrontendVersion.value}"
                Timber.d(msg)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        caseApp,
                        "前端已更新到 v${remoteFrontendVersion.value}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            return Result.success(Unit)
        } else {
            Timber.w("前端文件下载失败,请检查网络环境")
            withContext(Dispatchers.Main) {
                Toast.makeText(caseApp, "前端文件下载失败,请检查网络环境", Toast.LENGTH_SHORT)
                    .show()
            }
            return Result.failure(Exception("Failed to download frontend"))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateBackend(): Result<Unit> {
        withContext(Dispatchers.Main) {
            Timber.d("Updating backend to ${remoteBackendVersion.value}")
            Toast.makeText(
                caseApp,
                "正在将后端更新到 v${remoteBackendVersion.value}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Start download backend
        val script0Result = GithubUtil.downloadFile(
            REPO_BACKEND,
            remoteBackendVersion.value,
            "sub-store-0.min.js",
            caseApp.filesDir.absolutePath
        )

        val script1Result = GithubUtil.downloadFile(
            REPO_BACKEND,
            remoteBackendVersion.value,
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

            ConfigStore.localBackendVersion = remoteBackendVersion.value

            val msg = "Backend updated to ${remoteBackendVersion.value}"
            Timber.d(msg)
            withContext(Dispatchers.Main) {
                Toast.makeText(caseApp, msg, Toast.LENGTH_SHORT).show()
            }

            return Result.success(Unit)
        } else {
            Timber.w("后端文件下载失败,请检查网络环境")
            withContext(Dispatchers.Main) {
                Toast.makeText(caseApp, "后端文件下载失败,请检查网络环境", Toast.LENGTH_SHORT)
                    .show()
            }

            return Result.failure(Exception("Failed to download backend"))
        }
    }
}