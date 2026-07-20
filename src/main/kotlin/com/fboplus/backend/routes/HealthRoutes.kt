package com.fboplus.backend.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String, val service: String)

/**
 * Route de vérification simple : permet de confirmer que le serveur tourne
 * et répond, avant même de brancher l'authentification ou la base de données
 * dans les tests. Utile aussi pour la supervision (uptime monitoring).
 */
fun Routing.healthRoutes() {
    get("/health") {
        call.respond(HttpStatusCode.OK, HealthResponse(status = "ok", service = "fbo-plus-backend"))
    }
}
