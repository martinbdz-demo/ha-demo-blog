# Personal Blog

A simple personal blog built with Spring Boot, Thymeleaf, and an H2 in-memory database.

## Features

- Public front page listing all posts with excerpts
- Individual post detail pages
- Password-protected admin section to create and delete posts
- Pre-populated with sample content on startup

## Prerequisites

| Option | Requirements |
|---|---|
| Local | JDK 21+, Maven 3.8+ |
| Docker | Docker Engine |

## Running locally

```bash
# Build
mvn clean package -DskipTests

# Start
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

Open [http://localhost:8080](http://localhost:8080).

## Running with Docker

```bash
# Build the image (Maven runs inside the container — no local JDK needed)
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

## Admin section

Navigate to [http://localhost:8080/admin](http://localhost:8080/admin).

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `secret` |

From the dashboard you can publish new posts and delete existing ones.

## Useful URLs

| URL | Description |
|---|---|
| `http://localhost:8080/` | Public blog front page |
| `http://localhost:8080/post/{id}` | Individual post |
| `http://localhost:8080/admin` | Admin dashboard (requires login) |
| `http://localhost:8080/admin/login` | Login page |
| `http://localhost:8080/h2-console` | H2 database console (dev only) |

### H2 console connection settings

```
JDBC URL:  jdbc:h2:mem:blogdb
Username:  sa
Password:  (leave blank)
```

## Notes

- The H2 database is in-memory: all posts are lost when the application stops. To persist data, replace the datasource in `application.properties` with a file-based H2 URL (`jdbc:h2:file:./data/blogdb`) or a PostgreSQL/MySQL datasource.
- Thymeleaf template caching is disabled (`spring.thymeleaf.cache=false`) for development convenience. Set it to `true` in production.
- The H2 console should be disabled in production by setting `spring.h2.console.enabled=false`.
