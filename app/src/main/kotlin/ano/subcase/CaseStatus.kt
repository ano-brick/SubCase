package ano.subcase

import androidx.compose.runtime.mutableStateOf

object CaseStatus {
    var isWifi = mutableStateOf(false)
    var lanIP = mutableStateOf("")

    var isServiceRunning = mutableStateOf(false)

    var showUpdateDialog = mutableStateOf(false)
}