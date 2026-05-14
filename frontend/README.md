# LetsRoast Frontend

A minimal React + Vite frontend for the LetsRoast in-memory backend.

## What it does

- Create a user
- Create and list groups
- Join a group
- Post and load messages in the selected group

## Prerequisites

- Node.js 18+
- Backend running on `http://localhost:8080`

## Run

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

Vite is configured to proxy `/api` and `/actuator` calls to `http://localhost:8080`, so no backend CORS changes are required for local development.

## Build

```bash
npm run build
npm run preview
```

