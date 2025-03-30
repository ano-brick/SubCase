package ano.subcase.util

import android.content.Context
import android.content.SharedPreferences
import ano.subcase.util.PreferencesKeys.ALLOW_LAN
import ano.subcase.caseApp
import ano.subcase.util.PreferencesKeys.APP_IS_FIRST_OPEN
import ano.subcase.util.PreferencesKeys.BACKEND_LOCAL_VER
import ano.subcase.util.PreferencesKeys.FRONTEND_LOCAL_VER
import ano.subcase.util.PreferencesKeys.SERVICE_RUNNING

object PreferencesKeys {
    const val APP_IS_FIRST_OPEN = "app_first_open"
    const val CRASH_REPORT = "crash_report"
    const val SERVICE_RUNNING = "service_running"
    const val ALLOW_LAN = "allow_lan"

    const val BACKEND_LOCAL_VER = "backend_local_ver"
    const val FRONTEND_LOCAL_VER = "frontend_local_ver"

}

object ConfigStore {
    private var prefs: SharedPreferences? = null

    fun getInstance(): SharedPreferences {
        if (prefs == null) {
            prefs = caseApp.getSharedPreferences("settings", Context.MODE_PRIVATE)
        }
        return prefs!!
    }

    var isFirstOpen: Boolean
        get() = getInstance().getBoolean(APP_IS_FIRST_OPEN, true)
        set(value) {
            getInstance().edit().putBoolean(APP_IS_FIRST_OPEN, value).apply()
        }

    var isServiceRunning: Boolean
        get() = getInstance().getBoolean(SERVICE_RUNNING, false)
        set(value) {
            getInstance().edit().putBoolean(SERVICE_RUNNING, value).apply()
        }

    var isAllowLan: Boolean
        get() = getInstance().getBoolean(ALLOW_LAN, false)
        set(value) {
            getInstance().edit().putBoolean(ALLOW_LAN, value).apply()
        }

    var localFrontendVersion: String
        get() = getInstance().getString(FRONTEND_LOCAL_VER, "")!!
        set(value) {
            getInstance().edit().putString(FRONTEND_LOCAL_VER, value).apply()
        }

    var localBackendVersion: String
        get() = getInstance().getString(BACKEND_LOCAL_VER, "")!!
        set(value) {
            getInstance().edit().putString(BACKEND_LOCAL_VER, value).apply()
        }


}