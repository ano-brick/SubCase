package ano.subcase.util

import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

val REPO_BACKEND = "https://github.com/sub-store-org/Sub-Store"
val REPO_FRONTEND = "https://github.com/sub-store-org/Sub-Store-Front-End"


object GithubUtil {
    private val client = OkHttpClient()

    suspend fun getLatestVersion(repoUrl: String): String {

        val latestUrl = "$repoUrl/releases/latest"

        val request = Request.Builder().url(latestUrl).build()
        val response = client.newCall(request).execute()

        val latestReleaseUrl = response.networkResponse?.request?.url.toString()
        Timber.d("latest release url: $latestReleaseUrl")

        val latestVersion = latestReleaseUrl.substringAfterLast("/")
        println("LatestVersion: $latestVersion , RepoUrl: $repoUrl")
        return latestVersion
    }

    suspend fun downloadFile(projectUrl: String, version: String, fileName: String, destPath: String): Result<String> {
        try {
            // get download url
            val fileRequest =
                Request.Builder().url("$projectUrl/releases/download/$version/$fileName").build()
            val fileResponse = client.newCall(fileRequest).execute()

            if (!fileResponse.isSuccessful) {
                Timber.d("Failed to download file, response Code: ${fileResponse.code}")
                return Result.failure(Exception("Failed to download file"))
            }

            // download file
            val file = File(destPath + File.separator + fileName)
            file.outputStream().use { output ->
                fileResponse.body?.byteStream()?.use { input ->
                    input.copyTo(output)
                }
            }
            return Result.success("")
        } catch (e: Exception) {
            Timber.e(e)
            return Result.failure(e)
        }
    }

    fun renameFile(path: String, oldName: String, newName: String) {
        val oldPath = Path("$path/$oldName")
        val newPath = Path("$path/$newName")

        Files.move(oldPath, newPath)
    }
}