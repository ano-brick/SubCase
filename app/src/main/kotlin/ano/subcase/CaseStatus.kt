package ano.subcase

import androidx.compose.runtime.mutableStateOf
import ano.subcase.util.ConfigStore

object CaseStatus {
    var isAllowLan = mutableStateOf(ConfigStore.isAllowLan)
    var isWifi = mutableStateOf(false)
    var lanIP = mutableStateOf("")

    var isServiceRunning = mutableStateOf(false)

    var showUpdateDialog = mutableStateOf(false)
}