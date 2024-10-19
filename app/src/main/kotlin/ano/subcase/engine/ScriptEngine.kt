package ano.subcase.engine

import ano.subcase.caseApp
import ano.subcase.engine.extension.realToString
import ano.subcase.engine.`interface`.ConsoleInterface
import ano.subcase.engine.`interface`.HttpClientInterface
import ano.subcase.engine.`interface`.PersistentStoreInterface
import ano.subcase.model.Request
import ano.subcase.model.Response
import com.eclipsesource.v8.JavaVoidCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import timber.log.Timber
import java.io.File

class ScriptEngine() {
    private var engine: V8 = V8.createV8Runtime()

    // store v8 object ,and release it when destroy
    private var v8Objects = mutableListOf<V8Object>()

    private var response: Response = Response()

    init {
        Timber.d("V8 version: ${V8.getV8Version()}")

        // $loon
        engine.add("\$loon", "")

        // console
        val consoleObj = V8Object(engine)
        val consoleInterface = ConsoleInterface()
        consoleObj.registerJavaMethod(consoleInterface, "log", "log", arrayOf(String::class.java))
        engine.add("console", consoleObj)
        v8Objects.add(consoleObj)

        // $PersistentStore
        val persistentStoreObj = V8Object(engine)
        val persistentStoreInterface = PersistentStoreInterface()
        persistentStoreObj.registerJavaMethod(
            persistentStoreInterface,
            "write",
            "write",
            arrayOf(String::class.java, String::class.java)
        )
        persistentStoreObj.registerJavaMethod(persistentStoreInterface, "read", "read", arrayOf(String::class.java))
        persistentStoreObj.registerJavaMethod(persistentStoreInterface, "remove", "remove", arrayOf())
        engine.add("\$persistentStore", persistentStoreObj)
        v8Objects.add(persistentStoreObj)

        // $done
        val doneCallback = JavaVoidCallback { _, parameters ->
            Timber.d("doneCallback called with parameters: ${parameters.length()}")
            if (parameters.length() == 0) {
                Timber.d("doneCallback called with no parameters")
                return@JavaVoidCallback
            }

            val args = parameters[0] as V8Object
            v8Objects.add(args)
            val v8Response = args.get("response") as V8Object
            v8Objects.add(v8Response)

            val responseObj = Response()
            v8Response.get("status")?.let {
                responseObj.statusCode = it as Int
                Timber.d("status: $it")
            }
            // headers is v8 object
            v8Response.get("headers")?.let {
                v8Objects.add(it as V8Object)
                Timber.d("headers: " + it.realToString())
            }
            v8Response.get("body")?.let {
                responseObj.body = it as String
                Timber.d("body: $it")
            }

            response = responseObj
        }
        engine.registerJavaMethod(doneCallback, "\$done")

        // $httpClient
        val httpClientObj = V8Object(engine)
        v8Objects.add(httpClientObj)
        val httpClientInterface = HttpClientInterface { v8Objects.add(it) }
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "get",
            "get",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "post",
            "post",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "head",
            "head",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "delete",
            "delete",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "put",
            "put",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "options",
            "options",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        httpClientObj.registerJavaMethod(
            httpClientInterface,
            "patch",
            "patch",
            arrayOf(V8Object::class.java, V8Function::class.java)
        )
        engine.add("\$httpClient", httpClientObj)
    }

    fun process(request: Request): Result<Response> {
        Timber.d("Processing request: $request")

        // bind request object
        val headersObj = V8Object(engine)
        request.headers.forEach { (key, value) ->
            headersObj.add(key, value)
        }
        v8Objects.add(headersObj)

        val reqObj = V8Object(engine)
        reqObj.add("url", request.url)
        reqObj.add("method", request.method)
        reqObj.add("headers", headersObj)
        reqObj.add("body", request.body)
        engine.add("\$request", reqObj)
        v8Objects.add(reqObj)

        //
        val flag = matchRoute(request.url)
        if (flag == -1) {
            Timber.d("No matched route: ${request.url}")
            return Result.failure(Exception("No matched route: ${request.url}"))
        }

        val script = File(caseApp.filesDir, "backend/sub-store-$flag.min.js").readText()

        try {
            engine.executeVoidScript(script)
            Timber.d("executeScript finished")
        } catch (e: Exception) {
            Timber.d("executeScript error: %s", e.message)
            return Result.failure(e)
        } finally {
            try {
                v8Objects.forEach { it.close() }
                engine.release(true)
            } catch (e: Exception) {
                Timber.d("release error: %s", e.message)
            }
        }
        return Result.success(response)
    }

    fun registerAPIs() {

    }

    fun matchRoute(url: String): Int {
        //https://sub.store/api/utils/env
        val regex1 = Regex("^https?://sub.store/((download)|api/(preview|sync|(utils/node-info)))")
        val regex0 = Regex("^https?://sub.store")

        if (regex1.containsMatchIn(url)) {
            Timber.d("Matched route: $url")
            return 1
        } else if (regex0.containsMatchIn(url)) {
            Timber.d("Matched route: $url")
            return 0
        } else {
            Timber.d("No matched route: $url")
            return -1
        }
    }
}