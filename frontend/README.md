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

In local development, your browser talks to Vite on `http://localhost:5173`.

When the frontend calls `/api/...` or `/actuator/...`, Vite forwards that request to the backend at `http://localhost:8080` and returns the response.

So even if you hit `http://localhost:5173/api/users`, the backend on `8080` is the one actually handling it.

This is why local dev works without adding backend CORS setup.

## Build

```bash
npm run build
npm run preview
```

