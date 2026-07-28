package com.fboplus.backend.db

import com.fboplus.backend.config.Env
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DatabaseFactory {

    fun init() {
        val dataSource = createHikariDataSource()
        Database.connect(dataSource)
    }

    private fun createHikariDataSource(): DataSource {
        val dbUrl = Env.get("DATABASE_URL")
            ?: error("Variable d'environnement DATABASE_URL manquante. Voir README.md.")
        val dbUser = Env.get("DATABASE_USER") ?: "postgres"
        val dbPassword = Env.get("DATABASE_PASSWORD")
            ?: error("Variable d'environnement DATABASE_PASSWORD manquante. Voir README.md.")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = dbUrl
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