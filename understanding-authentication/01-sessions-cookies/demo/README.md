# Demo: TaskFlow with Spring sessions

TaskFlow is a small Spring Boot JSON API that uses Spring Security for signup, login, CSRF protection,
authenticated routes, and logout. Spring Session JDBC stores each session in PostgreSQL, so a login
can survive a backend restart without application memory holding the session.

## Requirements

- Java 25
- Docker
- curl

## Run

From this `demo` directory, start PostgreSQL and wait until it is healthy:

```bash
docker compose up -d --wait
```

Start Spring Boot:

```bash
./gradlew bootRun
```

The API listens at `http://localhost:8080`.

## API

| Method | Endpoint | Access | CSRF | Success | Common failure |
|---|---|---|---|---|---|
| `POST` | `/api/auth/signup` | Public | Required | `201` | `403` without CSRF |
| `POST` | `/api/auth/login` | Public | Required | `200` | `401` for invalid credentials, `403` without CSRF |
| `GET` | `/api/auth/csrf` | Public | Not required | `200` | N/A |
| `GET` | `/api/auth/me` | Authenticated | Not required | `200` | `401` when signed out |
| `POST` | `/api/auth/logout` | Session optional | Required | `204` | `403` without CSRF |
| `GET` | `/api/tasks` | Authenticated | Not required | `200` | `401` when signed out |
| `POST` | `/api/tasks` | Authenticated | Required | `201` | `401` when signed out, `403` without CSRF |

## Code structure

- `api` contains HTTP controllers and request and response models.
- `core` contains domain models, business services, and repository interfaces.
- `infra` contains JPA entities, Spring Data repositories, and adapters that map entities to domain models.
- `config` contains Spring Security configuration.

The core services depend on repository interfaces rather than JPA. Spring injects the infrastructure adapters that implement those interfaces.

## Follow the session with curl

curl does not manage browser cookies unless you give it a cookie-jar file. Create a temporary one:

> Run all commands in this walkthrough in the same terminal session. The cookie-jar file is stored on
> disk, but the `COOKIE_JAR` and `XSRF_TOKEN` variables used by later commands exist only in the shell
> where you created them.

```bash
COOKIE_JAR=$(mktemp)
```

### Get the first CSRF token

Call the public CSRF endpoint before logging in:

```bash
curl -i -c "$COOKIE_JAR" http://localhost:8080/api/auth/csrf
```

The response exposes Spring Security's token details and sends the same token in an `XSRF-TOKEN`
cookie. Read the cookie value from the cookie jar:

```bash
XSRF_TOKEN=$(awk '$6 == "XSRF-TOKEN" { print $7 }' "$COOKIE_JAR")
printf '%s\n' "$XSRF_TOKEN"
```

The assignment itself prints nothing. The `printf` command displays the extracted value so you can
confirm that the cookie was stored.

### Sign up

Send the cookie jar and copy the CSRF token into the `X-XSRF-TOKEN` header:

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  -d '{"username":"salim","password":"secret123"}' \
  http://localhost:8080/api/auth/signup
```

The response is `201 Created`. Authentication rotates the session ID and invalidates the CSRF token
that existed before login.

### Refresh the rotated CSRF token

Request a new token for the authenticated session:

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  http://localhost:8080/api/auth/csrf
XSRF_TOKEN=$(awk '$6 == "XSRF-TOKEN" { print $7 }' "$COOKIE_JAR")
printf '%s\n' "$XSRF_TOKEN"
```

The response is `200 OK`. You can now confirm the current user separately:

```bash
curl -i -b "$COOKIE_JAR" http://localhost:8080/api/auth/me
```

The `me` response is `200 OK` and contains `{"username":"salim"}`.

### Create and list tasks

Create a task with the current CSRF token:

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  -d '{"title":"Read about sessions"}' \
  http://localhost:8080/api/tasks
```

List tasks. GET requests do not require the CSRF header:

```bash
curl -i -b "$COOKIE_JAR" http://localhost:8080/api/tasks
```

The state-changing request contains two HTTP headers and three relevant values:

```http
Cookie: SESSION=...; XSRF-TOKEN=...
X-XSRF-TOKEN: ...
```

The browser equivalent would attach both cookies automatically, while frontend code would copy the
readable `XSRF-TOKEN` value into `X-XSRF-TOKEN`.

## Exercises

### Remove the CSRF header

Repeat the task request without `X-XSRF-TOKEN`:

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -d '{"title":"This should be rejected"}' \
  http://localhost:8080/api/tasks
```

The expected response is `403 Forbidden`.

### Inspect the stored session

Query the Spring Session table:

```bash
docker exec -it taskflow-postgres \
  psql -U taskflow -d taskflow \
  -c 'SELECT primary_id, session_id, creation_time, last_access_time, expiry_time FROM spring_session;'
```

The `SESSION` cookie identifies a row whose authenticated security context is stored in
`spring_session_attributes`.

### Restart the backend

Stop Spring Boot with `Ctrl-C`, then start it again:

```bash
./gradlew bootRun
```

Repeat the `GET /api/auth/me` request with the same cookie jar. The expected response remains `200 OK`
because PostgreSQL stores the session outside the application process.

### Log out

Send a CSRF-protected logout request:

```bash
curl -i -X POST -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  http://localhost:8080/api/auth/logout
```

The expected response is `204 No Content`. The old session no longer authenticates requests:

```bash
curl -i -b "$COOKIE_JAR" http://localhost:8080/api/auth/me
```

The expected response is `401 Unauthorized`.

### Remove the cookie jar

When you finish the walkthrough, delete the temporary cookie jar and clear its shell variables:

```bash
rm -f "$COOKIE_JAR"
unset COOKIE_JAR XSRF_TOKEN
```
