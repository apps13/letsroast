# Script to setup and run LetsRoast with PostgreSQL on Windows

Write-Host "LetsRoast PostgreSQL Setup & Run" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# Check if Docker is running
try {
    docker ps > $null 2>&1
} catch {
    Write-Host "Docker is not running or not installed. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Start PostgreSQL
Write-Host "Starting PostgreSQL container..." -ForegroundColor Yellow
docker-compose up -d

# Wait for PostgreSQL to be ready
Write-Host "Waiting for PostgreSQL to be ready..." -ForegroundColor Yellow
$maxAttempts = 30
$attempts = 0

while ($attempts -lt $maxAttempts) {
    try {
        $result = docker exec letsroast-postgres pg_isready -U letsroast 2>&1
        if ($result -like "*accepting*") {
            Write-Host "PostgreSQL is ready!" -ForegroundColor Green
            break
        }
    } catch {
        # Connection not ready yet
    }

    $attempts++
    Start-Sleep -Seconds 1
}

if ($attempts -eq $maxAttempts) {
    Write-Host "PostgreSQL failed to start. Check logs with: docker logs letsroast-postgres" -ForegroundColor Red
    exit 1
}

# Build the backend
Write-Host "Building backend..." -ForegroundColor Yellow
Set-Location backend-java
mvn clean package -DskipTests

# Run the backend
Write-Host "Starting backend with PostgreSQL..." -ForegroundColor Green
Write-Host "Application will be available at http://localhost:8080" -ForegroundColor Cyan
java -jar target/backend-java-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres

