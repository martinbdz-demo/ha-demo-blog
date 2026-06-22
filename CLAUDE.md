# Blog App — Claude Context

Personal blog application built with Spring Boot 3.3, Thymeleaf, Spring Security, and H2.

## Tech stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3.5, Java 21 |
| Templates | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Database | H2 in-memory (create-drop on startup) |
| Security | Spring Security — in-memory user, BCrypt |
| Build | Maven |

## Package structure

```
com.example.blog
├── BlogApplication.java         # Entry point
├── config/
│   ├── SecurityConfig.java      # Auth rules, login/logout URLs, CSRF config
│   └── DataInitializer.java     # Seeds 3 sample posts via ApplicationRunner
├── model/
│   └── BlogPost.java            # JPA entity: id, title, excerpt, content, createdAt
├── repository/
│   └── BlogPostRepository.java  # findAllByOrderByCreatedAtDesc()
├── service/
│   └── BlogPostService.java     # Thin wrapper: findAll, findById, save, deleteById
└── controller/
    ├── PublicController.java    # GET /, GET /post/{id}
    └── AdminController.java     # GET+POST /admin, /admin/posts, /admin/posts/{id}/delete
```

Templates live in `src/main/resources/templates/` (public) and `templates/admin/` (protected).

## Build and run

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

App starts at http://localhost:8080 in ~2 seconds.

## Admin credentials

- URL: http://localhost:8080/admin
- Username: `admin`
- Password: `secret`

Credentials are hardcoded in `SecurityConfig.java` (`InMemoryUserDetailsManager`).

## Key URLs

| URL | Notes |
|---|---|
| `http://localhost:8080/` | Public front page |
| `http://localhost:8080/post/{id}` | Post detail |
| `http://localhost:8080/admin/login` | Login page |
| `http://localhost:8080/admin` | Dashboard (auth required) |
| `http://localhost:8080/h2-console` | DB console — JDBC: `jdbc:h2:mem:blogdb`, user: `sa`, no password |


## Important notes

- **Database is in-memory**: all data is lost on restart. `DataInitializer` re-seeds on every start.
- **Thymeleaf caching** is off (`spring.thymeleaf.cache=false`) — template changes apply on restart without rebuild.
- **CSRF**: Thymeleaf injects tokens automatically via `th:action`. Raw `curl` POSTs will get 403 unless the CSRF token is extracted and included.

## CLAUDE.md Updates

Please keep this file updated in case you learn more about the application. Don't be too detailed on updated this - only for the important things. 