# Reverie — Backend

REST API for Reverie, a movie tracking application where users keep a
personal watch history and write reviews.

Built with Java 21, Spring Boot 3.5, PostgreSQL and Flyway. Authentication
uses stateless JWT access tokens, and authorization is capability based:
each role owns a set of fine grained capabilities, and every protected
endpoint requires a specific one.

> For running the whole application with Docker, see the
> [README in the project root](../README.md). This file covers developing
> the backend on its own.

## Requirements

- **JDK 21**
- **PostgreSQL 14 or newer** — the container image is 16
- No local Gradle installation — the wrapper is included

## Database

The application connects to a database that must already exist. Flyway
creates the tables, not the database itself.

```sql
CREATE USER reverie_user WITH PASSWORD '12345';
CREATE DATABASE reverie_db OWNER reverie_user;
```

The user owns the database rather than merely holding privileges on it.
Since PostgreSQL 15 the `public` schema no longer grants `CREATE` to
everyone, so a user who was only given `GRANT ALL PRIVILEGES ON DATABASE`
cannot create a single table and Flyway stops with *permission denied for
schema public*. Ownership is also what the container does: the official
image makes `POSTGRES_USER` the owner of `POSTGRES_DB`.

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

Interactive documentation: **http://localhost:8080/swagger-ui/index.html**

To produce the runnable jar instead:

```bash
./gradlew build
```

It lands in `build/libs/` and needs no local Gradle, no database and no
network to be produced — only the unit tests run.

## Migrations

Flyway runs automatically on startup. Migrations live in two locations:

| Location | Contents | Loaded in |
|---|---|---|
| `db/migration` | Schema, roles, capabilities | every profile |
| `db/demo` | Demonstration accounts and sample data | `dev` only |

Reference data the application cannot work without — roles, capabilities
and their assignments — belongs to the main chain. Demonstration data does
not, because it creates accounts whose credentials are publicly known in
this repository. Any profile other than `dev` therefore comes up with an
empty catalogue and no accounts at all.

This project ships only the `dev` profile, and `compose.yaml` activates it
on purpose: the point of the deployment is to be looked at, and an empty
catalogue shows nothing. The separation is what makes the other choice
available, not a claim that it has been taken.

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
| `nora@reverie.com` | `User123!` | USER |
| `theo@reverie.com` | `User123!` | USER |
| `iris@reverie.com` | `User123!` | USER |
| `milo@reverie.com` | `User123!` | USER |
| `ruth@reverie.com` | `User123!` | USER |
| `sam@reverie.com` | `User123!` | USER |

The dataset contains 12 genres, 24 films, 116 recorded viewings, 95 reviews
and 10 watchlist entries spread across the nine regular accounts. The
accounts deliberately watch the same films as one another, so every film
carries between three and eight ratings — enough for an average to mean
something and for a disagreement to be visible.

## Verifying the API

Two layers, and the line between them is deliberate: a test belongs to the
lower one if it can be decided without anything running.

### Unit tests

```bash
./gradlew test
```

Twenty-two tests across seven services. They build no Spring context, open
no connection and need nothing started, which is why they finish in seconds
and why `./gradlew build` succeeds on a machine that has just cloned this
repository.

They carry the rules that can be settled from the arguments alone: a restore
with nothing to restore, a favourite film the reader never watched, an
administrator demoting their own account, a review with neither a rating nor
any words.

Three of them exist for a sharper reason. They cover states the smoke test
cannot reach at all:

- a film keeping its own TMDB identifier while being edited. The duplicate
  check looks the identifier up and always finds the very film being edited,
  so it has to compare identities; without that, nothing could be saved
  twice
- the call that takes a film off the watchlist once it has been watched.
  The watchlist tests exercise that service directly, so if this call
  disappeared nothing would turn red
- an overview of a catalogue nobody has watched, where the leading film has
  to come back empty rather than naming a film with no viewings. The
  demonstration data can never produce that state

### Smoke test

```bash
bash ../scripts/smoke-test.sh
```

143 checks against a running application: every endpoint, every capability,
every business rule, every error status. Authentication, authorization, JSON
shapes, pagination, ordering and the database itself live here, and none of
them is visible to a unit test.

It reads back whatever it is about to overwrite and restores it afterwards,
so running it twice is the same as running it once.

### Interactive

The OpenAPI document at `/v3/api-docs` imports directly into Postman, which
turns every endpoint into a ready collection without anything being written
by hand. Swagger UI serves the same document at `/swagger-ui/index.html`.

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
├── security/     JWT filter, user details, error handlers, configuration
└── service/      Business logic
```

## Troubleshooting

**`APPLICATION FAILED TO START` mentioning `JWT_SECRET`**
The variable is not set, or the terminal was opened before `setx` ran.
Open a new terminal.

**`permission denied for schema public`**
The database exists but its user does not own it. Since PostgreSQL 15,
privileges on a database are not privileges on the schema inside it, so
Flyway cannot create a single table. Recreate it with `OWNER`, as shown
above, or hand ownership over:
`ALTER DATABASE reverie_db OWNER TO reverie_user;`

**`FATAL: database "reverie_db" does not exist`**
Flyway creates tables inside a database, not the database itself. Create
it first, as shown above.

**`Port 8080 was already in use`**
A previous run is still alive. On Windows:
`Get-NetTCPConnection -LocalPort 8080 -State Listen`

**`Validate failed: migration checksum mismatch`**
An already applied migration was edited. During development the simplest
fix is to drop and recreate the database.