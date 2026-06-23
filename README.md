# Personal Blog

A personal blog with a React frontend and a Spring Boot REST API backend.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React 19, React Router, Vite |
| Backend | Spring Boot 3.3, Spring Security, Spring Data JPA |
| Database | H2 in-memory (resets on restart, pre-seeded with 3 posts) |
| Build | Maven (backend), npm (frontend) |

## Prerequisites

| Component | Requirement |
|---|---|
| Backend | JDK 21+, Maven 3.8+ |
| Frontend | Node.js 18+ |

## Running locally

The backend and frontend run as separate processes during development. Vite proxies all `/api/*` requests to the Spring Boot server, so no CORS configuration is needed.

### 1. Start the backend

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`.

### 2. Start the frontend

```bash
cd frontend
npm install   # first time only
npm run dev
```

App available at `http://localhost:5173`.

## Running with Docker

```bash
# Build the image (Maven and npm both run inside the container)
DOCKER_BUILDKIT=1 docker build -t blog-app .

# Start
docker run -p 8080:8080 blog-app

# Run in the background
docker run -d -p 8080:8080 --name blog blog-app

# View logs
docker logs -f blog

# Stop
docker stop blog && docker rm blog
```

> **Note:** The Dockerfile currently only builds the Spring Boot backend. To serve the React frontend from the same container, build it first (`cd frontend && npm run build`) and copy the output into `src/main/resources/static/` before running the Docker build.

## Admin section

Navigate to `http://localhost:5173/admin` (or `http://localhost:8080/admin` if serving the built frontend).

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `secret` |

From the dashboard you can publish new posts and delete existing ones.

## API endpoints

### Public

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/posts` | List all posts |
| `GET` | `/api/posts/{id}` | Get a single post |

### Auth

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Login — body: `{"username":"…","password":"…"}` |
| `POST` | `/api/auth/logout` | Logout |
| `GET` | `/api/auth/me` | Current user (401 if not logged in) |

### Admin (requires login)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/admin/posts` | Create a post |
| `DELETE` | `/api/admin/posts/{id}` | Delete a post |

## Running tests

```bash
mvn test
```

## Useful URLs

| URL | Description |
|---|---|
| `http://localhost:5173/` | Blog front page (dev) |
| `http://localhost:5173/admin` | Admin dashboard (dev) |
| `http://localhost:8080/api/posts` | Raw API response |
| `http://localhost:8080/h2-console` | H2 database console (dev only) |

### H2 console connection settings

```
JDBC URL:  jdbc:h2:mem:blogdb
Username:  sa
Password:  (leave blank)
```

## Notes

- The H2 database is in-memory — all posts are lost when the backend restarts. Switch to a file-based URL (`jdbc:h2:file:./data/blogdb`) or a PostgreSQL datasource in `application.properties` to persist data.
- To serve everything from a single origin in production: run `npm run build` inside `frontend/`, copy the `dist/` contents to `src/main/resources/static/`, then build and run the Spring Boot jar.
