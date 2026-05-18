# LetsRoast Backend (Spring Boot)

This folder contains the Java backend API for LetsRoast.

> This project is also a personal exercise to learn Spring Boot fundamentals while building a real chat backend.

In simple terms:
- Backend = the server logic and API endpoints
- Frontend = the UI that calls this backend (in `../frontend`)

## What the backend does

- Starts a Spring Boot server on `http://localhost:8080`
- Exposes health & metrics endpoints
  - `GET /api/health` - Health check
  - `GET /actuator/health` - Detailed health
  - `GET /actuator/metrics` - Application metrics
- Supports full chat flow with persistent data:
  - Users - register and retrieve
  - Groups - create, list, and manage membership
  - Messages - post and retrieve group messages

## Data Persistence

**Two modes available:**

- **Default (PostgreSQL)** - The main mode used by this project
  - Data persists across restarts
  - Works with the included Docker setup
  - Better match for how the app is intended to run

- **H2 In-Memory** - Used for initial testing and quick experiments
  - Data stored in memory
  - Auto-cleared on restart
  - No external dependencies

## Main folders

- `src/main/java/com/letsroast/api` - REST controllers (HTTP routes)
- `src/main/java/com/letsroast/service` - business logic (in-memory & Postgres implementations)
- `src/main/java/com/letsroast/repository` - Spring Data JPA repositories for database access
- `src/main/java/com/letsroast/model` - JPA entities (with @Entity annotations)
- `src/main/resources` - app config and static assets

## Run backend

From this folder (`backend-java`):

### Default Mode (PostgreSQL)
```bash
mvn clean spring-boot:run
```

If you want to use the older in-memory mode for quick local testing, run:

```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=default"
```

### PostgreSQL Mode
```bash
# From project root, start database
cd ..
docker-compose up -d

# Back in backend-java folder
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=postgres"
```

## Quick checks

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/actuator/health
```

## Database

Tables auto-created by Hibernate:
- `users` - User accounts
- `groups` - Chat groups
- `chat_messages` - Messages in groups
- `user_group_membership` - Group membership tracking

## How this connects to frontend

- Frontend lives in `../frontend`
- Frontend runs on `http://localhost:5173`
- Vite dev proxy forwards `/api` and `/actuator` calls to `http://localhost:8080`

So the frontend and backend run as two separate stacks during development.

## Tech Stack

- **Framework**: Spring Boot 3.3.5
- **ORM**: Hibernate with Spring Data JPA
- **Build**: Maven
- **Java**: 17+
- **Databases**: H2 (dev), PostgreSQL (prod)
