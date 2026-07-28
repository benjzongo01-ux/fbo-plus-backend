package com.fboplus.backend

import com.fboplus.backend.config.Env
import com.fboplus.backend.db.DatabaseFactory
import com.fboplus.backend.plugins.configureCORS
import com.fboplus.backend.plugins.configureSerialization
import com.fboplus.backend.plugins.configureStatusPages
import com.fboplus.backend.routes.healthRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    val port = Env.get("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    configureSerialization()
    configureCORS()
    configureStatusPages()
    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        healthRoutes()
    }
}