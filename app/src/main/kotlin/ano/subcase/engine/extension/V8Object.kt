package ano.subcase.engine.extension

import com.eclipsesource.v8.V8Object


fun V8Object.getInteger(key: String, default: Int = 0): Int {
    return try {
        this.getInteger(key)
    } catch (e: Exception) {
        default
    }
}

fun V8Object.getString(key: String, default: String = ""): String {
    return try {
        this.getString(key)
    } catch (e: Exception) {
        default
    }
}

fun V8Object.realToString(): String {
    var result = "{"
    this.keys.forEach {
        val value = this[it]
        result += if (value is V8Object) {
            "$it: ${value.realToString()}, "
        } else {
            "$it: $value, "
        }
    }
    result += "}"

    return result
}