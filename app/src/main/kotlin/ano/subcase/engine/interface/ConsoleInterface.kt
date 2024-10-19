package ano.subcase.engine.`interface`

import timber.log.Timber

class ConsoleInterface {
    fun log(msg: String) {
        Timber.d("[console]$msg")
    }
}