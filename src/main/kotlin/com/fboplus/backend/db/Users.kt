package com.fboplus.backend.db

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 30).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val pinHash = varchar("pin_hash", 255).nullable()
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}