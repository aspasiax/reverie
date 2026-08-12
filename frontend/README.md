# Reverie — Frontend

Single page interface for Reverie, a film journal. Readers browse a
catalogue, record what they watched and write reviews; administrators
manage the catalogue and the accounts behind it.

Built with React 19, TypeScript 6 and Vite 8. Server state is handled by
TanStack Query, styling by Tailwind CSS 4 with shadcn/ui components, and
routing by React Router.

> For running the whole application with Docker, see the
> [README in the project root](../README.md). This file covers developing
> the interface on its own.

## Requirements

- **Node 22** or newer
- A running Reverie API — see [backend/README.md](../backend/README.md)

## Running

```bash
npm install
npm run dev
```

The dev server listens on `http://localhost:5173` and expects the API on
`http://localhost:8080`.

| Command | What it does |
|---|---|
| `npm run dev` | Development server with hot reload |
| `npm run build` | Type check, then produce the production bundle in `dist/` |
| `npm run preview` | Serve the production bundle locally |
| `npm run lint` | Run Oxlint |

## Configuration

One variable, read by Vite at build time:

| Variable | Default | Description |
|---|---|---|
| `VITE_API_URL` | `http://localhost:8080` | Address of the API |

The default suits development, where the dev server and the API are on
different ports. In the container image the value is `/`, which makes the
client send its requests to its own origin, where nginx forwards
everything under `/api` to the API.

Vite substitutes this **when the bundle is built**, not when it runs. The
address ends up compiled into the JavaScript, so changing it means
building again rather than restarting anything.

## Project layout

```
src/
├── auth/
│   └── AuthContext.tsx     Signed in user, sign in and sign out
├── components/
│   ├── ui/                 shadcn/ui primitives
│   ├── AppLayout.tsx       Header, navigation, the shell every screen shares
│   ├── ProtectedRoute.tsx  Guards everything behind the sign in wall
│   ├── AdminRoute.tsx      Guards the administration area
│   ├── GenreEditor.tsx     Create and edit dialogs
│   ├── MovieEditor.tsx
│   ├── ReviewEditor.tsx
│   ├── WatchActions.tsx    Recording a viewing and the viewing history
│   └── TmdbAttribution.tsx
│   ├── WatchActions.tsx    Recording a viewing and the viewing history
│   ├── WatchlistButton.tsx Adding a film to the watchlist and taking it off
│   └── TmdbAttribution.tsx
├── lib/
│   ├── api.ts              axios instance, token storage, interceptors
│   ├── capabilities.ts     The permissions the interface reacts to
│   ├── images.ts           Builds TMDB image addresses
│   └── utils.ts            Class name helper used by the ui components
├── pages/
│   ├── admin/              One file per administration screen
│   └── *.tsx               One file per screen
└── types/
    └── api.ts              The shapes the API sends and receives
```

## How data is fetched

Every request goes through the axios instance in `lib/api.ts`. Two
interceptors do the work that would otherwise be repeated everywhere: one
attaches the access token to each outgoing request, the other watches for
`401` and ends the session.

Only `401` is treated that way. A `403` means the caller is known but
lacks a capability, which is not a reason to sign anyone out.

### Query keys

Server state lives in TanStack Query, never in `useState`. A key is not a
label — it is the identity of a cache entry, and it is what makes
invalidation work.

| Key | Contents |
|---|---|
| `['movies', order, search, genre, page]` | One page of the catalogue for one selection |
| `['movies', 'manage', page]` | One page of the administration listing |
| `['movies', 'deleted']` | Films that were deleted |
| `['movie', uuid]` | A single film |
| `['genres']` | Active genres |
| `['genres', 'deleted']` | Genres that were deleted |
| `['reviews', 'movie', uuid]` | Reviews of one film |
| `['reviews', 'me']` | Reviews written by the signed in user |
| `['watchlist']` | Films the user intends to watch |
| `['watch-logs']` | The viewing history |
| `['users']` | Accounts, for administration |

Keys are arrays so that invalidation can work by prefix. Invalidating
`['movies']` refreshes the catalogue, the administration listing and the
recycle bin at once, because all three begin with it. That is why a film
deleted in the administration screen disappears from the catalogue without
anything having to say so explicitly.

The same idea connects screens that look unrelated: changing a genre
invalidates `['movies']` too, because films carry their genres inside
their own response and would otherwise keep showing an old badge.

### Mutations

Mutations never edit the cache by hand. They invalidate the keys their
change affects and let the server say what the new state is. Recalculating
a count or a sort order in the browser is a second implementation of a
rule that already exists on the server, and the two drift.

## Authorisation in the interface

The API grants permissions through capabilities such as `MOVIE_CREATE` or
`USER_DISABLE`, and checks one on every protected endpoint. The signed in
user's profile carries the list, and the interface makes its decisions
from exactly that list:

```tsx
const { can } = useAuth()

const deleteButton = can('GENRE_DELETE') && <Button>Delete</Button>
```

Checking the role name instead would mean two systems answering the same
question in different ways, and a new role would break the interface while
the API kept working.

**This only decides what is offered.** Nothing here protects anything: a
hidden button is not a closed door. Every request is authorised again by
the API, which answers `403` whatever the interface believes.

## Routing

```
/login              sign in
/register           create an account
/                   the catalogue          ┐
/movies/:uuid       one film               │ ProtectedRoute
/watchlist          films to watch         │
/watch-logs         viewing history        │
/my-reviews         reviews written        │
/profile            the signed in user     ┘
/admin/movies       films                  ┐
/admin/genres       genres                 │ ProtectedRoute + AdminRoute
/admin/users        accounts               ┘
```

`ProtectedRoute` waits for the stored token to be verified before deciding
anything. Redirecting during that moment would send every reload to the
sign in screen and straight back again.

`AdminRoute` is nested inside it, so by the time it runs the profile is
already loaded and only the capabilities are left to check.

## Building for production

```bash
npm run build
```

`tsc -b` runs first, so a type error fails the build rather than reaching
the bundle. The output in `dist/` is plain static files, which is what the
container image serves through nginx.

## Troubleshooting

**Every request fails with a network error**
The API is not running, or it is not where `VITE_API_URL` expects it.

**Requests are blocked by CORS in development**
The dev server and the API are different origins, so the API has to allow
`http://localhost:5173`. The allowed origins live in
`backend/src/main/resources/application-dev.properties`. In the
containerised deployment this cannot happen: nginx serves both from one
origin.

**A change to the API address has no effect**
It is substituted at build time. Restart the dev server, or rebuild the
image.

**Signed out unexpectedly**
Access tokens last an hour and there is no refresh token. Any `401` ends
the session, which is also what happens if the account was disabled while
it was signed in.