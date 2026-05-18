# LetsRoast

A full-stack chat app where users can register, create groups, and post messages in real time.

Built as a personal project to learn **Spring Boot** and put together a complete full-stack system from scratch — covering REST API design, cookie-based auth, database persistence, testing, and CI.

---

## What it does

- Register and sign in with a username and password
- Create chat groups or join existing ones
- Post and load messages inside a group
- Sessions persist via HTTP-only cookies
- Data persists via PostgreSQL (or runs fully in-memory for local dev with no setup)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React, Vite |
| Backend | Java, Spring Boot 3, Spring Data JPA |
| Database | PostgreSQL (prod) / H2 in-memory (dev) |
| Auth | Cookie-based sessions with BCrypt password hashing |
| Testing | JUnit 5, Mockito, Spring MockMvc |
| CI | GitHub Actions |

---

## Running Locally

### Quickest start (no database needed)

```bash
# Terminal 1 — backend (H2 in-memory)
cd backend-java
mvn clean spring-boot:run

# Terminal 2 — frontend
cd frontend
npm install
npm run dev
