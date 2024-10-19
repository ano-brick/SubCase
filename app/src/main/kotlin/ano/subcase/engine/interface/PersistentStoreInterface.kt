package ano.subcase.engine.`interface`

import android.content.Context
import android.content.SharedPreferences
import ano.subcase.caseApp
import timber.log.Timber


/**
 * 本地存储
 *
 * https://nsloon.app/LoonManual/#/cn/script_api?id=%e6%9c%ac%e5%9c%b0%e5%ad%98%e5%82%a8
 */

class PersistentStoreInterface {

    private var prefs: SharedPreferences? = null

    fun getInstance(): SharedPreferences {
        if (prefs == null) {
            prefs = caseApp.getSharedPreferences("persistent_store", Context.MODE_PRIVATE)
        }
        return prefs!!
    }

    fun write(value: String, key: String) {
        Timber.d("write $key $value")
        getInstance().edit().putString(key, value).apply()
    }

    fun read(key: String): String {
        Timber.d("read $key")
        return getInstance().getString(key, "")!!
    }

    // remove all key
    fun remove() {
        Timber.d("remove all")
        getInstance().edit().clear().apply()
    }
}