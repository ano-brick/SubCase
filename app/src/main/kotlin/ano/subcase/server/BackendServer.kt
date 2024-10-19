package ano.subcase.server

import ano.subcase.R
import ano.subcase.caseApp
import ano.subcase.engine.ScriptEngine
import ano.subcase.model.Request
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import org.slf4j.event.Level
import org.slf4j.impl.StaticLoggerBinder
import timber.log.Timber

class BackendServer(
    port: Int = 8080,
    allowLan: Boolean = false
) {
    private var clock: () -> Long = { getTimeMillis() }

    private val server = embeddedServer(
        factory = Netty,
        port = port,
        host = if (allowLan) "0.0.0.0" else "127.0.0.1"
    ) {
        routing {
            route("{...}") {
                handle {
                    val method = call.request.httpMethod.value
                    val uri = call.request.uri
                    val fullUrl = "https://sub.store$uri"

                    val headers = emptyMap<String, String>().toMutableMap()

                    call.request.headers.entries().forEach { (key, value) ->
                        Timber.d("$key: $value")
                        headers[key] = value.joinToString(separator = ",")
                    }

                    Timber.d(headers.toString())

                    val request = Request(
                        method = method,
                        url = fullUrl,
                        headers = headers,
                        body = call.receiveText()
                    )

                    val response = ScriptEngine().process(request)
                    if (response.isFailure) {
                        call.respondText(response.exceptionOrNull()?.message ?: "Internal Server Error")
                        return@handle
                    } else {
                        val resp = response.getOrNull()!!
                        resp.headers.forEach { (key, value) ->
                            call.response.headers.append(key, value)
                        }
                        call.response.status(HttpStatusCode.fromValue(resp.statusCode))

                        call.respondText(resp.body)
                    }
                }
            }
        }

        install(CORS) {
            anyHost()
        }

        install(CallLogging) {
            level = Level.INFO
            logger = StaticLoggerBinder.getSingleton().loggerFactory.getLogger(
                caseApp.resources.getString(R.string.app_name)
            )
            format { call ->
                "${call.response.status()}: ${call.request.httpMethod.value} - ${call.request.path()} in ${
                    call.processingTimeMillis(clock)
                }ms"
            }
        }
    }

    fun start() {
        Timber.d("Starting backend server")
        server.start(wait = false)
    }

    fun stop() {
        Timber.d("Stopping backend server")
        server.stop(1000, 1000)
    }
}