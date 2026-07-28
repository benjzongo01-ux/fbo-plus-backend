package com.fboplus.backend.config

import io.github.cdimascio.dotenv.dotenv

/**
 * Point d'accès centralisé aux variables d'environnement.
 *
 * En local : charge le fichier .env à la racine du projet.
 * En production (Railway, etc.) : le fichier .env n'existe pas, donc on
 * bascule automatiquement sur les vraies variables d'environnement du
 * système (ignoreIfMissing = true évite un crash si .env est absent).
 */
object Env {
    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    fun get(key: String): String? = dotenv[key] ?: System.getenv(key)
}