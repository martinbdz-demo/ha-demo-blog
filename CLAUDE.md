# Blog App — Claude Context

Personal blog with a React SPA frontend and a Spring Boot REST API backend.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React 19, React Router, Vite (port 5173) |
| Backend | Spring Boot 3.3.5, Java 21 (port 8080) |
| Database | H2 in-memory (create-drop, re-seeded on every restart) |
| Security | Spring Security — session cookies, BCrypt, `@PreAuthorize` on controllers |
| Build | Maven (backend) · npm (frontend) |

---

## Project layout

```
ha-demo-app/
├── src/                        # Spring Boot backend
│   ├── main/java/com/example/blog/
│   │   ├── BlogApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      # REST security: session, 401/403, no CSRF
│   │   │   └── DataInitializer.java     # Seeds 3 posts via ApplicationRunner
│   │   ├── model/BlogPost.java          # JPA entity: id, title, excerpt, content, createdAt
│   │   ├── repository/BlogPostRepository.java
│   │   ├── service/BlogPostService.java
│   │   └── controller/
│   │       ├── PostApiController.java   # GET /api/posts, GET /api/posts/{id}
│   │       ├── AdminApiController.java  # POST/DELETE /api/admin/posts (ADMIN only)
│   │       ├── AuthController.java      # POST /api/auth/login|logout, GET /api/auth/me
│   │       ├── LoginRequest.java        # record {username, password}
│   │       └── SpaController.java       # Forwards non-asset paths to index.html for React Router
│   └── main/resources/application.properties
├── src/test/java/com/example/blog/     # JUnit 5 + MockMvc tests
└── frontend/                           # Vite + React SPA
    ├── vite.config.js                  # Proxies /api/* → localhost:8080
    └── src/
        ├── App.jsx                     # Router, AuthContext, ProtectedRoute
        ├── api.js                      # fetch wrapper for all API calls
        └── pages/
            ├── Home.jsx                # Lists posts (GET /api/posts)
            ├── PostDetail.jsx          # Single post (GET /api/posts/:id)
            ├── Login.jsx               # Login form (POST /api/auth/login)
            └── Admin.jsx               # Dashboard: create + delete posts
```

---

## Running the app

```bash
# Backend — runs on http://localhost:8080
mvn spring-boot:run

# Frontend — runs on http://localhost:5173 (Vite proxies /api/* to :8080)
cd frontend && npm run dev
```

## Building

```bash
# Backend fat jar
mvn clean package -DskipTests

# Frontend production build
cd frontend && npm run build
# Copy frontend/dist/ contents to src/main/resources/static/ to serve from Spring Boot
```

## Tests

```bash
mvn test
```

18 tests across 5 classes. All `@WebMvcTest` classes must `@Import(SecurityConfig.class)` — Spring Boot's web slice does not auto-discover it. Surefire needs `-Dnet.bytebuddy.experimental=true` (already in pom.xml) because the machine runs Java 26, which Byte Buddy doesn't officially support yet.

---

## API reference

### Public
- `GET /api/posts` → `[{id, title, excerpt, createdAt}]`
- `GET /api/posts/{id}` → `{id, title, excerpt, content, createdAt}` or 404

### Auth
- `POST /api/auth/login` body `{username, password}` → `{username}` or 401
- `POST /api/auth/logout` → 200
- `GET /api/auth/me` → `{username}` or 401

### Admin (session required, ROLE_ADMIN)
- `POST /api/admin/posts` body `{title, excerpt, content}` → 201 + post or 400
- `DELETE /api/admin/posts/{id}` → 204

## Admin credentials

Hardcoded in `SecurityConfig.java` (`InMemoryUserDetailsManager`):
- Username: `admin` · Password: `secret`

---

## Key design decisions

- **CSRF disabled** — the API is same-origin in production (Spring Boot serves the React build from `/static`). During development, Vite proxy makes all requests same-origin to `:5173`. No CSRF risk in either scenario.
- **Session cookies, not JWT** — simpler for a single-user personal blog. `JSESSIONID` is managed transparently by the browser through the Vite proxy.
- **`@PreAuthorize` on controllers** — access control lives at the method level, not in URL matchers in `SecurityConfig`. The `anyRequest().permitAll()` rule is intentional; `@PreAuthorize` is the enforcer for admin routes.
- **`@Import(SecurityConfig.class)` in tests** — `@WebMvcTest` does not auto-scan `@EnableWebSecurity` configs; explicit import is required for form login redirect and `@PreAuthorize` to work in the test slice.
- **H2 in-memory** — data resets on every restart. `DataInitializer` re-seeds 3 posts each time. Switch to `jdbc:h2:file:./data/blogdb` or PostgreSQL in `application.properties` to persist.

---

## Docker

Multi-stage build: Node builds the React SPA, Maven packages Spring Boot with the frontend baked into `src/main/resources/static/`, JRE runs the final JAR.

```bash
docker build -t blog-app .
docker run -p 8080:8080 blog-app
# App available at http://localhost:8080
```

`SpaController.java` forwards all non-asset, non-API paths to `index.html` so React Router handles client-side navigation correctly (e.g. refreshing `/posts/1` doesn't 404).

## Update CLAUDE.md

After every change make sure to update this instruction set anything interesting. 