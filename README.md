# FBO+ Backend

API centrale de la plateforme FBO+ — écrite en **Kotlin** avec le framework **Ktor**,
base de données **PostgreSQL**.

## Pourquoi cette stack

- **Kotlin** : même langage que l'app mobile FBO+ (Android), pour rester cohérent et
  réutiliser les mêmes compétences.
- **Ktor** : framework web créé par JetBrains (créateurs de Kotlin), léger, rapide,
  basé sur les coroutines (déjà utilisées dans l'app mobile via `RappelWorker`).
- **Exposed** : ORM Kotlin (aussi JetBrains) pour interagir avec la base de données
  de façon typée et sécurisée (protection native contre les injections SQL).
- **PostgreSQL** : base de données relationnelle robuste, standard de l'industrie.

## Prérequis

1. **IntelliJ IDEA** (Community suffit) — télécharger sur https://www.jetbrains.com/idea/download/
   (Android Studio ne convient pas pour un projet backend pur)
2. **JDK 21** (Java) — souvent déjà installé si Android Studio est installé
3. **Docker Desktop** (pour lancer une base PostgreSQL locale facilement) —
   https://www.docker.com/products/docker-desktop/

## Démarrage en local — étape par étape

### 1. Cloner le projet
```bash
git clone https://github.com/TON-USERNAME/fbo-plus-backend.git
cd fbo-plus-backend
```

### 2. Lancer la base de données locale
```bash
docker compose up -d
```
Ça démarre une base PostgreSQL sur ta machine, isolée, avec les identifiants définis
dans `docker-compose.yml` (uniquement pour le dev local — jamais utilisés en production).

### 3. Configurer les variables d'environnement
```bash
cp .env.example .env
```
Le fichier `.env.example` fourni fonctionne tel quel avec la base Docker locale.
`.env` est ignoré par Git (voir `.gitignore`) — il ne sera jamais envoyé sur GitHub,
c'est volontaire et important pour la sécurité (jamais commiter de mots de passe).

### 4. Ouvrir le projet dans IntelliJ IDEA
- **File → Open** → sélectionner le dossier `fbo-plus-backend`
- IntelliJ va détecter `build.gradle.kts` et télécharger automatiquement les
  dépendances (Ktor, Exposed, etc.) et générer le wrapper Gradle — ça peut prendre
  quelques minutes la première fois.

### 5. Lancer le serveur
- Ouvrir `src/main/kotlin/com/fboplus/backend/Application.kt`
- Cliquer sur le bouton ▶️ à côté de `fun main()`
- Le serveur démarre sur `http://localhost:8080`

### 6. Vérifier que ça fonctionne
Ouvre dans un navigateur (ou avec `curl`) :
```
http://localhost:8080/health
```
Tu devrais voir :
```json
{"status": "ok", "service": "fbo-plus-backend"}
```

Si tu vois ça, le backend tourne et la connexion est prête pour la suite
(authentification, wallet, commandes...).

## Structure du projet

```
src/main/kotlin/com/fboplus/backend/
├── Application.kt         # Point d'entrée du serveur
├── db/
│   └── DatabaseFactory.kt # Connexion à PostgreSQL
├── plugins/
│   ├── Serialization.kt   # Conversion JSON
│   ├── CORS.kt            # Autorisation des appels depuis l'app mobile
│   └── StatusPages.kt     # Gestion centralisée des erreurs
├── routes/
│   └── HealthRoutes.kt    # Route de test /health
└── models/                # (à venir : modèles de données — User, Wallet, Commande...)
```

## Prochaine étape

Authentification (module Identity) : inscription, connexion, gestion des rôles,
jetons JWT — voir la roadmap générale du projet.
