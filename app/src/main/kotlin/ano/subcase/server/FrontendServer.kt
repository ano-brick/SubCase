package ano.subcase.server

import ano.subcase.R
import ano.subcase.caseApp
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import org.slf4j.event.Level
import org.slf4j.impl.StaticLoggerBinder
import timber.log.Timber
import java.io.File

class FrontendServer(
    port: Int = 8081,
    allowLan: Boolean = false
) {
    private var clock: () -> Long = { getTimeMillis() }

    private val server = embeddedServer(
        factory = Netty,
        port = port,
        host = if (allowLan) "0.0.0.0" else "127.0.0.1"
    ) {
        routing {
            staticFiles(
                "/",
                File("${caseApp.filesDir.path}/frontend"),
                index = "index.html",
            ) {
                default("index.html")
            }
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
        Timber.d("Starting frontend server")
        server.start(wait = false)
    }

    fun stop() {
        Timber.d("Stopping frontend server")
        server.stop(1000, 1000)
    }
}