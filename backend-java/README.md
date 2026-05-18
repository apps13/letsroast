# LetsRoast Backend (Spring Boot)
# Testing Workflow

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

- **Default (H2 In-Memory)** - Zero setup, perfect for development
  - Data stored in memory
  - Auto-cleared on restart
  - No external dependencies

- **PostgreSQL** - Production-ready persistence
  - Data persists across restarts
  - Docker container included
  - Scalable to multiple instances

## Main folders

- `src/main/java/com/letsroast/api` - REST controllers (HTTP routes)
- `src/main/java/com/letsroast/service` - business logic (in-memory & Postgres implementations)
- `src/main/java/com/letsroast/repository` - Spring Data JPA repositories for database access
- `src/main/java/com/letsroast/model` - JPA entities (with @Entity annotations)
- `src/main/resources` - app config and static assets

## Run backend

From this folder (`backend-java`):

### Default Mode (H2 In-Memory)
```bash
mvn clean spring-boot:run
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

## API Examples

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "alice"}'

# Create a group
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{"name": "roast_squad", "createdBy": "alice_id"}'

# Post a message
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"groupId": "group_id", "userId": "alice_id", "message": "This code is fire!"}'

# Get messages
curl http://localhost:8080/api/messages?groupId=group_id
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
