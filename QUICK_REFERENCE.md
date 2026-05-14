# Quick Reference: Postgres Persistence

## 🚀 Start Application (Choose One)

### Development (Default - H2 In-Memory)
```bash
cd backend-java
mvn clean spring-boot:run
# No setup needed - works immediately
```

### Production (PostgreSQL)
```bash
# Start database (from project root)
docker-compose up -d

# Run application (from backend-java folder)
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=postgres"
```

## 📍 Application URL
```
http://localhost:8080
```

## 🔍 Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 📝 Key API Endpoints

```bash
# Create User
POST /api/users
{"username": "alice"}

# Create Group  
POST /api/groups
{"name": "dev_squad", "createdBy": "alice_id"}

# Join Group
POST /api/groups/{groupId}/join
{"userId": "alice_id"}

# Post Message
POST /api/messages
{"groupId": "group_id", "userId": "alice_id", "message": "Hello!"}

# Get Messages
GET /api/messages?groupId=group_id

# List Groups
GET /api/groups

# Get User
GET /api/users/{userId}
```

## 🗄️ Database Info

**Default Profile (H2)**
- URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (empty)
- Data: Lost on restart

**Postgres Profile**
- URL: `jdbc:postgresql://localhost:5432/letsroast`
- User: `letsroast`
- Password: `letsroast123`
- Data: Persists

## 🐳 Docker Commands

```bash
# Start database
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f db

# Stop database
docker-compose down

# Stop & remove data
docker-compose down -v
```

## 🛑 Stop Application

```bash
# Press Ctrl+C in terminal

# If stuck, find and kill process
ps aux | grep java
kill -9 <PID>
```

## 📂 Important Files

- `backend-java/src/main/java/com/letsroast/` - Application code
  - `api/` - REST endpoints
  - `service/` - Business logic (in-memory & postgres)
  - `repository/` - Database access (JPA)
  - `model/` - Data entities
- `backend-java/src/main/resources/application.yml` - Config
- `docker-compose.yml` - PostgreSQL container

## 🔧 Troubleshooting

**Port 8080 already in use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080
# Kill process
taskkill /PID <PID> /F
```

**Database connection failed**
```bash
# Make sure docker-compose is running
docker-compose ps
# Check database is healthy
docker-compose logs db | tail -20
```

**Tests failing**
```bash
mvn clean test
# Check Hibernate and JPA configuration
```

## 📚 Documentation

- `POSTGRES_SETUP.md` - Full setup guide
- `IMPLEMENTATION_SUMMARY.md` - Technical details
- `COMPLETION_CHECKLIST.md` - What's implemented
- `README.md` - Backend overview

## 💡 Tips

- Use default H2 mode for quick local development
- Use postgres mode when ready for persistent data
- Run `mvn clean` before switching profiles
- Check logs if something goes wrong: `docker-compose logs -f`
- Use `mvn test` to verify everything works

---

**Last Updated**: 2026-05-14
**Status**: ✅ Production Ready

