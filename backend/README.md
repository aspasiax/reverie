# Reverie — Backend

REST API for Reverie, a movie tracking application where users keep a
personal watch history and write reviews.

Built with Java 21, Spring Boot 3.5, PostgreSQL and Flyway. Authentication
uses stateless JWT access tokens, and authorisation is capability based:
each role owns a set of fine grained capabilities, and every protected
endpoint requires a specific one.

> For running the whole application with Docker, see the
> [README in the project root](../README.md). This file covers developing
> the backend on its own.

## Requirements

- **JDK 21**
- **PostgreSQL 14 or newer**
- No local Gradle installation — the wrapper is included

## Database

The application connects to a database that must already exist. Flyway
creates the tables, not the database itself.

```sql
CREATE DATABASE reverie_db;
CREATE USER reverie_user WITH PASSWORD '12345';
GRANT ALL PRIVILEGES ON DATABASE reverie_db TO reverie_user;
```

Connection settings can be overridden through `PG_HOST`, `PG_PORT`,
`PG_DB`, `PG_USER` and `PG_PASSWORD`. All of them have development
defaults, so the values above work without any configuration.

## Configuration

| Variable | Required | Default | Description |
|---|---|---|---|
| `JWT_SECRET` | **yes** | — | Base64 encoded signing key, at least 32 bytes |
| `JWT_EXPIRATION_MS` | no | `3600000` | Token lifetime in milliseconds |
| `PG_HOST` | no | `localhost` | Database host |
| `PG_PORT` | no | `5432` | Database port |
| `PG_DB` | no | `reverie_db` | Database name |
| `PG_USER` | no | `reverie_user` | Database user |
| `PG_PASSWORD` | no | `12345` | Database password |

`JWT_SECRET` has no default on purpose: the application refuses to start
without one, so a signing key is never accidentally shipped in source
control. Generate one once and store it in the environment:

```powershell
$bytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
setx JWT_SECRET ([Convert]::ToBase64String($bytes))
```

Changing the secret invalidates every token that was already issued.

## Running

```bash
./gradlew bootRun
```

The API listens on `http://localhost:8080`.

Interactive documentation: **http://localhost:8080/swagger-ui.html**

## Migrations

Flyway runs automatically on startup. Migrations live in two locations:

| Location | Contents | Loaded in |
|---|---|---|
| `db/migration` | Schema, roles, capabilities | every profile |
| `db/demo` | Demonstration accounts and sample data | `dev` only |

Reference data the application cannot work without — roles, capabilities
and their assignments — belongs to the main chain. Demonstration data does
not, because it creates accounts whose credentials are publicly known in
this repository. Keeping the two apart means a production deployment never
receives them.

Demo migrations use a high version range (`V901` and above) so they can
never collide with future schema changes. Because a new schema migration
therefore has a lower version than demo data that has already run,
out of order migrations are enabled in the development profile.

## Demo accounts

Available only when the `dev` profile is active.

| Email | Password | Role |
|---|---|---|
| `admin@reverie.com` | `Admin123!` | ADMIN |
| `alex@reverie.com` | `User123!` | USER |
| `emma@reverie.com` | `User123!` | USER |
| `daniel@reverie.com` | `User123!` | USER |

The dataset contains 12 genres, 24 films, 25 recorded viewings, 15
reviews and 10 watchlist entries spread across the three regular
accounts.

## Verifying the API

A smoke test exercises every endpoint, capability check, business rule and
error response against a running application:

```bash
bash ../scripts/smoke-test.sh
```

It leaves the database exactly as it found it.

## Project layout

```
src/main/java/io/github/aspasiax/reverie/
├── config/       OpenAPI configuration
├── controller/   REST endpoints
├── domain/       JPA entities
├── dto/          Request and response records
├── exception/    Custom exceptions and the global handler
├── mapper/       Entity to DTO conversion
├── repository/   Spring Data repositories
├── security/     JWT filter, user details, security configuration
└── service/      Business logic
```

## Troubleshooting

**`APPLICATION FAILED TO START` mentioning `JWT_SECRET`**
The variable is not set, or the terminal was opened before `setx` ran.
Open a new terminal.

**`FATAL: database "reverie_db" does not exist`**
Flyway creates tables inside a database, not the database itself. Create
it first, as shown above.

**`Port 8080 was already in use`**
A previous run is still alive. On Windows:
`Get-NetTCPConnection -LocalPort 8080 -State Listen`

**`Validate failed: migration checksum mismatch`**
An already applied migration was edited. During development the simplest
fix is to drop and recreate the database.