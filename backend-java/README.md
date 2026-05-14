# LetsRoast Backend (Spring Boot)

This folder contains the Java backend API for LetsRoast.

In simple terms:
- Backend = the server logic and API endpoints
- Frontend = the UI that calls this backend (in `../frontend`)

## What the backend does right now

- Starts a Spring Boot server on `http://localhost:8080`
- Exposes health endpoints
  - `GET /api/health`
  - `GET /actuator/health`
- Supports in-memory chat flow endpoints for:
  - users
  - groups
  - group membership
  - group messages

Data is in memory for now, so restarting the backend clears users/groups/messages.

## Main folders

- `src/main/java/com/letsroast/api` - REST controllers (HTTP routes)
- `src/main/java/com/letsroast/service` - business logic and in-memory implementations
- `src/main/java/com/letsroast/model` - core data models
- `src/main/resources` - app config and static assets

## Run backend

From this folder (`backend-java`):

```powershell
mvn spring-boot:run
```

## Quick checks

```powershell
curl http://localhost:8080/api/health
curl http://localhost:8080/actuator/health
```

## How this connects to frontend

- Frontend lives in `../frontend`
- Frontend runs on `http://localhost:5173`
- Vite dev proxy forwards `/api` and `/actuator` calls to `http://localhost:8080`

So the frontend and backend run as two separate stacks during development.

