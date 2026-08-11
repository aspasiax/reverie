# Reverie

> Your Movie Journey

A film journal: keep a record of what you watched and when, write reviews,
and browse a catalogue that can be ordered by what people actually watch
and rate.

Built as a full stack application with a Spring Boot REST API, a React
single page interface and PostgreSQL, packaged so that the whole thing
starts with one command.

---

## Contents

- [What it does](#what-it-does)
- [Built with](#built-with)
- [Running it](#running-it) — the short version
- [Build and deploy](#build-and-deploy) — what actually happens
- [Running without Docker](#running-without-docker)
- [Demo accounts](#demo-accounts)
- [Verifying the API](#verifying-the-api)
- [Project structure](#project-structure)
- [How it is put together](#how-it-is-put-together)
- [Troubleshooting](#troubleshooting)
- [Attribution](#attribution)

---

## What it does

**For everyone**

- Browse a catalogue of films, ordered alphabetically, by how often they
  have been watched, or by average rating
- Record a viewing, with or without a date — a film seen three times is
  three entries, because a rewatch is a real event
- Write one review per film, with a rating, written text, or both
- Keep a profile and a personal viewing history

**For administrators**

- Manage films: create, edit, publish, withdraw, delete and restore
- Manage genres, including permanently destroying ones already deleted
- Manage accounts: change roles, disable and re-enable access

Every administrative control is shown only to accounts that hold the
capability it needs. The interface hides what it cannot use; the API
refuses it regardless.

---

## Built with

| Layer | Technology |
|---|---|
| API | Java 21, Spring Boot 3.5.16, Spring Security, Spring Data JPA |
| Database | PostgreSQL 16, Flyway migrations |
| Authentication | Stateless JWT access tokens |
| Interface | React 19, TypeScript 6, Vite 8, Tailwind CSS 4 |
| Data fetching | TanStack Query 5, axios |
| Routing | React Router 7 |
| Packaging | Docker, nginx |

---

## Running it

**You need:** Docker Desktop. Nothing else — no JDK, no Node, no
PostgreSQL.

```bash
git clone https://github.com/aspasiax/reverie.git
cd reverie
cp .env.example .env
```

Open `.env` and set two values:

```bash
PG_PASSWORD=any-password-you-like
JWT_SECRET=a-long-random-string
```

Generate the signing key with:

```bash
openssl rand -base64 48
```

Then:

```bash
docker compose up -d --build
```

The first run takes a few minutes: it downloads the base images and
resolves every dependency. Subsequent runs are seconds.

**Open http://localhost:3000** and sign in with one of the
[demo accounts](#demo-accounts).

| Address | What |
|---|---|
| http://localhost:3000 | The application |
| http://localhost:3000/swagger-ui/index.html | Interactive API documentation |
| http://localhost:8080 | The API directly, for testing |
| localhost:5432 | PostgreSQL, for inspection |

To stop everything without losing data:

```bash
docker compose down
```

> **Never `docker compose down -v`** unless you mean it. The `-v` flag
> deletes the volume, and with it the entire database.

---

## Build and deploy

Three services are described in `compose.yaml` and start in dependency
order.

### 1. `postgres`

The official `postgres:16-alpine` image. Its data lives in a named volume
called `reverie-pgdata`, which is what makes the database survive
`docker compose down`, a restart of Docker, or a reboot.

A healthcheck runs `pg_isready` every five seconds. This matters: the
backend is configured to wait for `condition: service_healthy` rather than
merely for the container to exist. Without it the API would start,
find no database to migrate, and die.

### 2. `backend`

Built from `backend/Dockerfile` in two stages.

**The build stage** starts from a JDK image and runs the Gradle wrapper
inside the container, so no Java toolchain is needed on the host. The
wrapper and the build scripts are copied before the source, and
dependencies are resolved in their own layer — editing a Java file then
rebuilds only the final step instead of downloading everything again.

**The runtime stage** starts from a JRE image and copies in nothing but
the assembled jar. The JDK, Gradle and the entire source tree stay behind.
The result runs as an unprivileged user rather than as root.

On startup, Flyway brings the schema to the current version. All settings
arrive as environment variables, listed below.

### 3. `frontend`

Built from `frontend/Dockerfile`, also in two stages.

**The build stage** installs dependencies with `npm ci`, which reproduces
exactly the versions recorded in `package-lock.json`, and runs the Vite
production build.

**The runtime stage** is nginx serving the compiled assets. Node is not
part of the shipped image: the application at that point is plain HTML,
CSS and JavaScript.

nginx does two more jobs, both described in `frontend/nginx.conf`:

- **It proxies `/api` to the backend.** The interface and the API are
  therefore served from the same origin, and the browser never makes a
  cross-origin request. There is no CORS in the deployed application at
  all.
- **It serves `index.html` for any address that is not a file.** The
  router lives in the browser, so reloading `/movies/abc-123` has to
  return the application rather than a 404.

> **One thing to know:** Vite substitutes environment variables at
> **build** time, not at run time. The API address is compiled into the
> JavaScript bundle. That is why `frontend/Dockerfile` sets
> `ARG VITE_API_URL=/`, and why changing it means rebuilding the image
> rather than restarting the container.

### Configuration

Every value comes from `.env`, which is never committed. `.env.example`
records what is needed.

| Variable | Required | Default | Description |
|---|---|---|---|
| `JWT_SECRET` | **yes** | — | Signing key for access tokens, at least 32 characters |
| `PG_PASSWORD` | **yes** | — | Database password |
| `JWT_EXPIRATION_MS` | no | `3600000` | Token lifetime in milliseconds |
| `PG_DB` | no | `reverie_db` | Database name |
| `PG_USER` | no | `reverie_user` | Database user |

The two required variables are declared in `compose.yaml` as
`${JWT_SECRET:?...}`. The syntax is deliberate: compose refuses to start
and says which variable is missing, instead of quietly bringing up a
database with a blank password.

`PG_PASSWORD` only takes effect the first time the database is created.
Changing it later has no effect on an existing volume, and the backend
will report an authentication failure.

### Rebuilding after a change

```bash
docker compose up -d --build backend     # after changing Java code
docker compose up -d --build frontend    # after changing React code
docker compose up -d --build             # after changing both
```

Database migrations run automatically when the backend starts, so a
schema change needs nothing beyond rebuilding that one service.

### Useful commands

```bash
docker compose ps                  # what is running
docker compose logs -f backend     # follow the API logs
docker compose restart backend     # restart one service
docker compose down                # stop everything, keep the data
```

---

## Running without Docker

Useful while developing, when a rebuild per change would be too slow.

**You need:** JDK 21, Node 22, and a PostgreSQL instance.

The database can still come from Docker while the rest runs on the host:

```bash
docker compose up -d postgres
```

**API:**

```bash
cd backend
./gradlew bootRun
```

It listens on `http://localhost:8080`. `JWT_SECRET` must be present in the
environment — see [backend/README.md](backend/README.md) for the full
list of settings and how to generate a key.

**Interface:**

```bash
cd frontend
npm install
npm run dev
```

It listens on `http://localhost:5173` and talks to the API at
`http://localhost:8080`. Here the two are different origins, so CORS does
apply; the allowed origins are configured in
`backend/src/main/resources/application-dev.properties`.

---

## Demo accounts

Loaded by the `dev` profile, which is what `compose.yaml` activates.

| Email | Password | Role |
|---|---|---|
| `admin@reverie.com` | `Admin123!` | ADMIN |
| `alex@reverie.com` | `User123!` | USER |
| `emma@reverie.com` | `User123!` | USER |
| `daniel@reverie.com` | `User123!` | USER |

The dataset contains 12 genres, 24 films, 25 recorded viewings and 15
reviews spread across the three regular accounts, so the application has
something to show the moment it opens.

Demonstration data is kept in a separate Flyway location from the schema.
A deployment that does not activate the `dev` profile never receives
these accounts, whose passwords are published in this file.

---

## Verifying the API

A smoke test exercises every endpoint, capability check, business rule and
error response against a running application.

```bash
bash scripts/smoke-test.sh
```

Against the containerised stack, through the nginx proxy:

```bash
API=http://localhost:3000 bash scripts/smoke-test.sh
```

It runs 89 checks and leaves the database exactly as it found it.

---

## Project structure

```
reverie/
├── backend/              Spring Boot REST API
│   ├── src/main/java/    Controllers, services, repositories, security
│   ├── src/main/resources/
│   │   └── db/
│   │       ├── migration/  Schema, roles, capabilities
│   │       └── demo/       Demonstration data, dev profile only
│   ├── Dockerfile
│   └── README.md         Backend specific documentation
├── frontend/             React single page application
│   ├── src/
│   │   ├── auth/         Authentication context
│   │   ├── components/   Shared components and route guards
│   │   ├── lib/          API client, capabilities, helpers
│   │   ├── pages/        One file per screen
│   │   └── types/        Shapes shared with the API
│   ├── nginx.conf        Static serving and the API proxy
│   └── Dockerfile
├── docs/
│   └── decisions.md      Why the project is built the way it is
├── scripts/
│   └── smoke-test.sh     End to end verification
├── compose.yaml
└── .env.example
```

---

## How it is put together

**Layered on the server.** Controllers handle HTTP and nothing else,
services own the business rules, repositories talk to the database.
Entities never leave the service layer: every response is a DTO, which is
also what prevents a request from setting fields it has no business
setting.

**Capabilities, not roles.** No endpoint asks whether someone is an
administrator. Each one requires a specific capability such as
`MOVIE_CREATE` or `USER_DISABLE`, and roles are collections of those. The
interface makes its decisions from the same list, which the API sends with
the signed in user's profile — so both layers answer the same question the
same way.

**Tokens are not passports.** A JWT proves who the caller is; it is not
trusted on its own. The account behind it is loaded and checked on every
request, which is why disabling an account ends its session immediately
even though the token it holds is still perfectly valid.

**Nothing is really deleted.** Records are marked as deleted and can be
restored. Uniqueness rules are enforced by partial indexes that only apply
to live rows, so a deleted genre does not reserve its name forever. The
one exception is destroying a genre that has already been deleted, which
is irreversible and asks for confirmation.

The reasoning behind these choices, and several others, is recorded in
[docs/decisions.md](docs/decisions.md).

---

## Troubleshooting

**`Port 8080 was already in use`**
Something else is on that port — often a copy of the API started from an
IDE. Stop it, or comment out the `ports` entry of the `backend` service:
the interface reaches the API over the internal network and does not need
it published.

**The interface loads but every request fails with 502**
nginx is up before the API. Give it a few seconds, then check with
`docker compose logs backend`.

**`password authentication failed for user "reverie_user"`**
`PG_PASSWORD` in `.env` does not match the password the database was
created with. It is only applied when the volume is first created.

**A code change does not appear**
Rebuild the image; restarting is not enough. For the interface remember
that the API address is compiled into the bundle.

**Starting completely fresh**
This destroys the database and everything in it:

```bash
docker compose down -v
docker compose up -d --build
```

---

## Attribution

Film metadata and images come from
[The Movie Database](https://www.themoviedb.org/). This product uses the
TMDB API but is not endorsed or certified by TMDB.

The data was retrieved once and stored in the demonstration migrations, so
the application does not call TMDB at runtime and needs no API key.

---

Built as a final project for the Coding Factory programme, Athens
University of Economics and Business.