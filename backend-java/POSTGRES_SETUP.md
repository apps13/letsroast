# LetsRoast Backend

Spring Boot backend for the LetsRoast application with support for both in-memory and PostgreSQL persistence.

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for PostgreSQL setup)

## Running Locally

### Option 1: In-Memory Mode (Default)

Uses H2 in-memory database - perfect for development and testing.

```bash
cd backend-java
mvn clean spring-boot:run
```

The application starts on `http://localhost:8080`

### Option 2: PostgreSQL Mode (Production)

#### Start PostgreSQL with Docker

From the project root:

```bash
docker-compose up -d
```

This starts a PostgreSQL container with:
- Host: `localhost`
- Port: `5432`
- Username: `letsroast`
- Password: `letsroast123`
- Database: `letsroast`

#### Run the Application

```bash
cd backend-java
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=postgres"
```

Or set the environment variable:

```bash
# On Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE = "postgres"
mvn clean spring-boot:run

# On Linux/Mac
export SPRING_PROFILES_ACTIVE=postgres
mvn clean spring-boot:run
```

## Build

```bash
cd backend-java
mvn clean package
```

This creates an executable JAR at `target/backend-java-0.0.1-SNAPSHOT.jar`

### Run the JAR

```bash
# Default (H2 in-memory)
java -jar backend-java-0.0.1-SNAPSHOT.jar

# PostgreSQL mode
java -jar backend-java-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres
```

## API Endpoints

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Create User**: `POST /api/users` with JSON body `{"username": "user"}`
- **Get User**: `GET /api/users/{userId}`
- **Create Group**: `POST /api/groups` with JSON body `{"name": "group", "createdBy": "userId"}`
- **List Groups**: `GET /api/groups`
- **Join Group**: `POST /api/groups/{groupId}/join` with JSON body `{"userId": "userId"}`
- **Post Message**: `POST /api/messages` with JSON body `{"groupId": "groupId", "userId": "userId", "message": "text"}`
- **Get Messages**: `GET /api/messages?groupId={groupId}`

## Technology Stack

- **Framework**: Spring Boot 3.3.5
- **ORM**: Hibernate/JPA
- **Databases**:
  - H2 (default/testing)
  - PostgreSQL (production)
- **Build**: Maven
- **Java**: 17

## Database Schema

The application auto-creates tables on startup:

- `users` - User accounts
- `groups` - Chat groups
- `chat_messages` - Messages in groups
- `user_group_membership` - Group membership tracking

## Stopping PostgreSQL

```bash
docker-compose down
```

To remove the database volume:

```bash
docker-compose down -v
```

## Notes

- Default profile uses H2 in-memory database (data lost on restart)
- PostgreSQL profile persists data in Docker volume `letsroast_pgdata`
- Hibernate auto-creates/updates schema via `ddl-auto: update` (Postgres) or `create-drop` (H2)

