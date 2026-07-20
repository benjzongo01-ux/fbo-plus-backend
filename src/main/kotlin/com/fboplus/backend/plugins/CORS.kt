package com.fboplus.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

/**
 * Autorise l'app mobile FBO+ (et plus tard FBO+ Admin, FBO+ Delivery) à appeler l'API.
 * En développement on autorise tout (anyHost) ; à resserrer avant la mise en production
 * (n'autoriser que les domaines/apps officiels FBO+).
 */
fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // TODO: restreindre aux domaines officiels avant mise en production
    }
}
