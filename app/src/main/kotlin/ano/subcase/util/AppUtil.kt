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
        createDataDir()
    }

    private fun createDataDir() {
        val dataDir = caseApp.filesDir.path + "/data"
        val dataDirFile = File(dataDir)
        if (!dataDirFile.exists()) {
            dataDirFile.mkdirs()
        }
    }

    private fun extractBackendFile() {
        SubStore.localBackendVersion = "2.19.13"

        val assetManager = caseApp.assets

        //mkdir scripts
        val scriptsDir = caseApp.filesDir.path + "/backend"
        val scriptsDirFile = File(scriptsDir)
        if (!scriptsDirFile.exists()) {
            scriptsDirFile.mkdirs()
        }

        var inputStream = assetManager.open("backend/sub-store.bundle.js")
        var dataFile = caseApp.filesDir.path + "/backend" + "/sub-store.bundle.js"
        var outputStream = FileOutputStream(dataFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    private fun extractFrontendDist() {
        SubStore.localFrontendVersion = "2.15.12"

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

        Files.deleteIfExists(Paths.get(caseApp.filesDir.path + "/dist.zip"))
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