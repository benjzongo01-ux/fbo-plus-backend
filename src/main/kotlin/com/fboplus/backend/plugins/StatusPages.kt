package com.fboplus.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)

/**
 * Gestion centralisée des erreurs : évite d'exposer des détails techniques
 * (stack traces, messages internes) au client — important pour la sécurité.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Requête invalide"))
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Erreur non gérée", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Une erreur interne est survenue."))
        }
    }
}
