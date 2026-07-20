package com.fboplus.backend

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
    // Port lu depuis la variable d'environnement PORT (fournie par l'hébergeur),
    // avec 8080 comme valeur par défaut pour le développement local.
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. Connexion à la base de données (PostgreSQL)
    DatabaseFactory.init()

    // 2. Plugins transverses (JSON, CORS, gestion d'erreurs, logs)
    configureSerialization()
    configureCORS()
    configureStatusPages()
    install(CallLogging) {
        level = Level.INFO
    }

    // 3. Routes
    routing {
        healthRoutes()
    }
}
