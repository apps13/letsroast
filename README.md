# Let's Roast

LetsRoast is a backend service for private, invite-only groups where users can post anonymous compliments (and optionally roasts).

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Docker

## Current Status

- Spring Boot baseline is set up in `backend-java`
- Learning path is intentionally incremental (small steps)

## Project Structure

- `backend-java/src/main/java/com/letsroast`: Spring Boot app entrypoint
- `backend-java/src/main/java/com/letsroast/api`: REST controllers

## Phase 1 (Current): Boot + Health Endpoint

1. Run the backend from `backend-java`.
2. Verify the app is running with a health check.

```powershell
cd backend-java
mvn spring-boot:run
```

Health check:

```powershell
curl http://localhost:8080/api/health
```

## Suggested Next Steps (Learning Order)

1. Add one simple `User` model class.
2. Add one `UserController` endpoint (`POST /api/users`) with in-memory list storage inside the controller.
3. Extract storage into a `UserRepository` interface + in-memory implementation.
4. Add `Post` model and endpoints.
5. Add validation with `spring-boot-starter-validation`.


