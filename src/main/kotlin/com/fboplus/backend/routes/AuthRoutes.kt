package com.fboplus.backend.routes

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fboplus.backend.config.JwtConfig
import com.fboplus.backend.db.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class RegisterRequest(val email: String, val phone: String, val password: String)

@Serializable
data class LoginRequest(val identifier: String, val password: String) // email ou téléphone

@Serializable
data class SetPinRequest(val pin: String)

@Serializable
data class LoginPinRequest(val phone: String, val pin: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class ErrorResponse(val error: String)

private fun isValidPin(pin: String): Boolean = pin.length == 4 && pin.all { it.isDigit() }

fun Routing.authRoutes() {
    route("/auth") {

        post("/register") {
            val body = call.receive<RegisterRequest>()

            if (body.password.length < 8) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Le mot de passe doit contenir au moins 8 caractères."))
                return@post
            }

            // Compte de test réservé : toujours réinscriptible, sans blocage 409
            val estCompteTest = body.email == "test@fboplus.com" && body.phone == "+22600000000"

            val existingUserId = transaction {
                Users.selectAll().where { (Users.email eq body.email) or (Users.phone eq body.phone) }
                    .firstOrNull()?.get(Users.id)
            }

            if (existingUserId != null) {
                if (estCompteTest) {
                    val hash = BCrypt.hashpw(body.password, BCrypt.gensalt())
                    transaction {
                        Users.update({ Users.id eq existingUserId }) {
                            it[passwordHash] = hash
                            it[pinHash] = null
                        }
                    }
                    call.respond(HttpStatusCode.Created, AuthResponse(JwtConfig.generateToken(existingUserId)))
                    return@post
                } else {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("Un compte existe déjà avec cet email ou ce numéro."))
                    return@post
                }
            }

            val hash = BCrypt.hashpw(body.password, BCrypt.gensalt())

            val newUserId = transaction {
                Users.insert {
                    it[email] = body.email
                    it[phone] = body.phone
                    it[passwordHash] = hash
                    it[createdAt] = System.currentTimeMillis()
                } get Users.id
            }

            call.respond(HttpStatusCode.Created, AuthResponse(JwtConfig.generateToken(newUserId)))
        }

        post("/login") {
            val body = call.receive<LoginRequest>()

            val userData = transaction {
                val row = Users.selectAll().where { (Users.email eq body.identifier) or (Users.phone eq body.identifier) }.firstOrNull()
                if (row != null) Pair(row[Users.id], row[Users.passwordHash]) else null
            }

            if (userData == null || !BCrypt.checkpw(body.password, userData.second)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Identifiants incorrects."))
                return@post
            }

            call.respond(HttpStatusCode.OK, AuthResponse(JwtConfig.generateToken(userData.first)))
        }

        post("/set-pin") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token manquant."))
                return@post
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val userId: Int
            try {
                val decoded = JwtConfig.verifier.verify(token)
                userId = decoded.getClaim("userId").asInt()
            } catch (e: JWTVerificationException) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token invalide ou expiré."))
                return@post
            }

            val body = call.receive<SetPinRequest>()

            if (!isValidPin(body.pin)) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Le PIN doit contenir exactement 4 chiffres."))
                return@post
            }

            val hash = BCrypt.hashpw(body.pin, BCrypt.gensalt())

            transaction {
                Users.update({ Users.id eq userId }) {
                    it[pinHash] = hash
                }
            }

            call.respond(HttpStatusCode.OK, mapOf("message" to "PIN défini avec succès."))
        }

        post("/login-pin") {
            val body = call.receive<LoginPinRequest>()

            if (!isValidPin(body.pin)) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Le PIN doit contenir exactement 4 chiffres."))
                return@post
            }

            val userData = transaction {
                val row = Users.selectAll().where { Users.phone eq body.phone }.firstOrNull()
                if (row != null) Pair(row[Users.id], row[Users.pinHash]) else null
            }

            if (userData == null || userData.second == null || !BCrypt.checkpw(body.pin, userData.second)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("PIN incorrect ou non défini."))
                return@post
            }

            call.respond(HttpStatusCode.OK, AuthResponse(JwtConfig.generateToken(userData.first)))
        }
    }
}