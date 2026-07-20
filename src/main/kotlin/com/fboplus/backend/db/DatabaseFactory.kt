package com.fboplus.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

/**
 * Gère la connexion à la base de données PostgreSQL.
 *
 * Toutes les informations sensibles (URL, utilisateur, mot de passe) viennent
 * des variables d'environnement — JAMAIS écrites en dur dans le code, pour
 * la sécurité (voir README pour la configuration locale).
 */
object DatabaseFactory {

    fun init() {
        val dataSource = createHikariDataSource()
        Database.connect(dataSource)
    }

    private fun createHikariDataSource(): DataSource {
        // Ces variables sont définies :
        // - en local, dans un fichier .env (jamais commité, voir .gitignore)
        // - en production, dans les "Environment Variables" de l'hébergeur (Railway)
        val jdbcUrl = System.getenv("DATABASE_URL")
            ?: error("Variable d'environnement DATABASE_URL manquante. Voir README.md.")
        val dbUser = System.getenv("DATABASE_USER") ?: "postgres"
        val dbPassword = System.getenv("DATABASE_PASSWORD")
            ?: error("Variable d'environnement DATABASE_PASSWORD manquante. Voir README.md.")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = jdbcUrl
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
