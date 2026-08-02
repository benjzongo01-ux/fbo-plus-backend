package com.fboplus.backend.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private val secret = Env.get("JWT_SECRET") ?: error("JWT_SECRET manquant. Voir README.md.")
    private const val issuer = "fbo-plus-backend"
    private const val validityMs = 7L * 24 * 60 * 60 * 1000 // 7 jours

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun generateToken(userId: Int): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + validityMs))
        .sign(algorithm)
}