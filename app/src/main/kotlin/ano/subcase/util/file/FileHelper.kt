package ano.subcase.util.file

import ano.subcase.caseApp

object FileHelper {

    fun getAssetAsString(assetName: String): String {
        caseApp.assets.open(assetName).bufferedReader().use {
            return it.readText()
        }
    }
}