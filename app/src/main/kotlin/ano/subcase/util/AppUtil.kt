package ano.subcase.util

import ano.subcase.caseApp
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipInputStream

object AppUtil {
    fun initFirstOpen() {
        extractBackendFile()
        extractFrontendDist()
    }

    private fun extractBackendFile() {
        SubStore.localBackendVersion = "2.14.301"

        val assetManager = caseApp.assets

        //mkdir scripts
        val scriptsDir = caseApp.filesDir.path + "/backend"
        val scriptsDirFile = File(scriptsDir)
        if (!scriptsDirFile.exists()) {
            scriptsDirFile.mkdirs()
        }

        var inputStream = assetManager.open("backend/sub-store-0.min.js")
        var dataFile = caseApp.filesDir.path + "/backend" + "/sub-store-0.min.js"
        var outputStream = FileOutputStream(dataFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        inputStream = assetManager.open("backend/sub-store-1.min.js")
        dataFile = caseApp.filesDir.path + "/backend" + "/sub-store-1.min.js"
        outputStream = FileOutputStream(dataFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    private fun extractFrontendDist() {
        SubStore.localFrontendVersion = "2.14.230"

        val assetManager = caseApp.assets

        val zipInputStream = assetManager.open("frontend/dist.zip")

        val zipPath = caseApp.filesDir.path + "/dist.zip"
        val zipOutputStream = FileOutputStream(zipPath)
        zipInputStream.copyTo(zipOutputStream)
        zipInputStream.close()
        zipOutputStream.close()

        //  unzip dist.zip ,will gen dist dir
        unzip(File(zipPath), File(caseApp.filesDir.path))

        if (Files.exists(Paths.get(caseApp.filesDir.path + "/dist"))) {
            Files.move(
                Paths.get(caseApp.filesDir.path + "/dist"),
                Paths.get(caseApp.filesDir.path + "/frontend")
            )
        } else {
            Timber.w("Failed to move dist to frontend")
        }

        if (Files.exists(Paths.get(caseApp.filesDir.path + "/dist.zip"))) {
            Files.delete(Paths.get(caseApp.filesDir.path + "/dist.zip"))
        } else {
            Timber.w("Failed to delete dist.zip")
        }
    }

    fun unzip(zipFile: File, destination: File) {

        ZipInputStream(zipFile.inputStream()).use { zis ->
            generateSequence { zis.nextEntry }.forEach { zipEntry ->
                val newFile = File(destination, zipEntry.name)
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.outputStream().use { fos ->
                        zis.copyTo(fos)
                    }
                }
            }
        }
    }

}