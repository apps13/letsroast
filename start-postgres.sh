#!/bin/bash

# Script to setup and run LetsRoast with PostgreSQL

set -e

echo "LetsRoast PostgreSQL Setup & Run"
echo "================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Start PostgreSQL
echo -e "${YELLOW}Starting PostgreSQL container...${NC}"
docker-compose up -d

# Wait for PostgreSQL to be ready
echo -e "${YELLOW}Waiting for PostgreSQL to be ready...${NC}"
MAX_ATTEMPTS=30
ATTEMPTS=0
while [ $ATTEMPTS -lt $MAX_ATTEMPTS ]; do
    if docker exec letsroast-postgres pg_isready -U letsroast >/dev/null 2>&1; then
        echo -e "${GREEN}PostgreSQL is ready!${NC}"
        break
    fi
    ATTEMPTS=$((ATTEMPTS + 1))
    sleep 1
done

if [ $ATTEMPTS -eq $MAX_ATTEMPTS ]; then
    echo -e "${RED}PostgreSQL failed to start. Check logs with: docker logs letsroast-postgres${NC}"
    exit 1
fi

# Build the backend
echo -e "${YELLOW}Building backend...${NC}"
cd backend-java
mvn clean package -DskipTests

# Run the backend
echo -e "${GREEN}Starting backend with PostgreSQL...${NC}"
java -jar target/backend-java-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres

