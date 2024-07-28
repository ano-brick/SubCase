package ano.subcase.util

import ano.subcase.caseApp

object SubStore {

    val basePath = caseApp.filesDir

    var frontendLocalVer: String
        get() = ConfigStore.frontendLocalVer
        set(value) {
            ConfigStore.frontendLocalVer = value
        }

    var backendLocalVer: String
        get() = ConfigStore.backendLocalVer
        set(value) {
            ConfigStore.backendLocalVer = value
        }

    fun backendLatestVer(): String {
        return "1.0.0"
    }

    fun frontendLatestVer(): String {
        return "1.0.0"
    }

}