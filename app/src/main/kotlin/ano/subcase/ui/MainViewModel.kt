package ano.subcase.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ano.subcase.util.ConfigStore

class MainViewModel : ViewModel() {

    var allowLan by mutableStateOf(false)

    init {
        if (ConfigStore.isAllowLan) {
            allowLan = true
        }

        if (ConfigStore.isServiceRunning) {

        }
    }

}