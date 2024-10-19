package ano.subcase.engine.`interface`

import ano.subcase.engine.extension.getInteger
import ano.subcase.engine.extension.getString
import ano.subcase.engine.extension.realToString
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * 网络请求
 * https://nsloon.app/LoonManual/#/cn/script_api?id=%e7%bd%91%e7%bb%9c%e8%af%b7%e6%b1%82
 */
class HttpClientInterface(
    val storeV8Object: (V8Object) -> Unit
) {

    /**
     * $httpClient.get(params, function(errormsg,response,data){}):
     */
    fun get(params: V8Object, callback: V8Function) {
        doHttpRequest("GET", params, callback)
    }

    /**
     * $httpClient.post(params, function(errormsg,response,data){}):
     */
    fun post(params: V8Object, callback: V8Function) {
        doHttpRequest("POST", params, callback)
    }

    fun head(params: V8Object, callback: V8Function) {
        doHttpRequest("HEAD", params, callback)
    }

    fun delete(params: V8Object, callback: V8Function) {
        doHttpRequest("DELETE", params, callback)
    }

    fun put(params: V8Object, callback: V8Function) {
        doHttpRequest("PUT", params, callback)
    }

    fun options(params: V8Object, callback: V8Function) {
        doHttpRequest("OPTIONS", params, callback)
    }

    fun patch(params: V8Object, callback: V8Function) {
        doHttpRequest("PATCH", params, callback)
    }

    private fun doHttpRequest(method: String, requestObj: V8Object, callback: V8Function) {
        try {
            storeV8Object(requestObj)
            storeV8Object(callback)

            val url = requestObj.getString("url")
            val timeout = requestObj.getInteger("timeout", 5000)
            val body = requestObj.getString("body", "")
            val headersObj = requestObj.getObject("headers")
            storeV8Object(requestObj)

            val headersMap = mutableMapOf<String, String>()
            headersObj.keys.forEach {
                headersMap[it] = headersObj.getString(it)
            }
            storeV8Object(headersObj)

            val client = HttpClient()
            runBlocking {
                val response: HttpResponse = client.request(url) {
                    this.method = HttpMethod.parse(method)
                    timeout {
                        requestTimeoutMillis = timeout.toLong()
                    }
                    headers {
                        headersMap.forEach { (key, value) ->
                            append(key, value)
                        }
                    }
                    setBody(body, TypeInfo(String::class))
                }
                val responseObj = V8Object(requestObj.runtime)
                responseObj.add("status", response.status.value)

                val headerObj = V8Object(requestObj.runtime)
                response.headers.entries().forEach { (key, value) ->
                    headerObj.add(key, value.joinToString(separator = ";"))
                }
                responseObj.add("headers", headerObj)
                storeV8Object(headerObj)

                val respBody = response.bodyAsText()
                Timber.d("responseObj" + responseObj.realToString())
                Timber.d("responseBody: $respBody")

                val params = V8Array(requestObj.runtime).push("").push(responseObj).push(respBody)
                callback.call(requestObj.runtime, params)
                storeV8Object(params)
            }
        } catch (e: Exception) {
            Timber.e(e)

            val responseObj = V8Object(requestObj.runtime)
            storeV8Object(responseObj)

            val params = V8Array(requestObj.runtime).push(e.message).push(responseObj).push("")
            storeV8Object(params)
            callback.call(
                requestObj.runtime,
                params
            )
        }
    }
}