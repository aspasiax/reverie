# Design decisions

This document records the decisions that shaped Reverie and the reasoning
behind them. The code shows what was built; this file explains why it was
built that way, and what the rejected alternatives were.

## Authorization through capabilities rather than roles alone

A role in Reverie does not grant permissions directly. Each role owns a set
of fine grained capabilities such as `MOVIE_CREATE` or `REVIEW_UPDATE`, and
every protected endpoint requires one specific capability rather than a
role name.

The simpler alternative would have been to annotate endpoints with
`hasRole('ADMIN')`. That works until the permissions stop matching the
roles exactly. Adding a moderator who may edit and delete reviews but not
create films would mean revisiting every annotation in the application. In
the capability model the same change is a row in `role_capabilities`, with
no code change at all.

The cost is a more complex data model: two extra tables and a join on every
authentication. It is a deliberate trade of setup effort for flexibility
later.

## Soft delete with restore, instead of permanent deletion

Deleting a record sets a `deleted` flag and a timestamp instead of removing
the row. Every query that returns data filters on this flag, so deleted
records disappear from the API while remaining in the database.

Soft delete only makes sense if deletion can actually be undone, so films
and genres both have a restore endpoint. Without one, the flag would add
complexity to every query while behaving exactly like a permanent delete.

Permanent deletion was not implemented. The foreign keys from reviews and
watch logs cascade, so removing a user would silently erase all of their
reviews and viewing history with no way back. In a production system a
right to erasure request would force the feature to exist, but it would
need to be built carefully, most likely by anonymising the account rather
than deleting the rows.

## Uniqueness scoped to active records

The uniqueness constraints originally covered every row, including deleted
ones. That produced a subtle problem: deleting the genre "Horror" reserved
that name permanently, because the deleted row still occupied the unique
slot. The same applied to usernames and email addresses, so a deleted
account blocked anyone from registering with that address again.

The constraints were replaced with partial unique indexes that apply only
where `deleted = FALSE`. The genre name index also compares case
insensitively, which aligns the database with a check the service layer was
already performing.

The repository queries had to change in the same commit. They deliberately
included deleted rows, precisely because the old constraint did too. Had
only the database changed, the application would have stayed stricter than
the schema and the fix would have had no visible effect.

Not every constraint became partial. The `uuid` columns stay globally
unique because a public identifier should never be reused, even after
deletion. Roles and capabilities stay unique for the same reason: they are
reference data that is never deleted.

## Watching a film clears it from the watchlist

A watchlist entry records an intention rather than an event. Adding a film
states that the user means to see it, and a user holds that intention once:
a partial unique index over active rows allows a single entry per user and
film, following the same pattern as reviews. Because the index ignores
removed rows, a film taken off the list can be added back later.

When a viewing is recorded for a film that is on the list, the entry is
removed. The obvious alternative was to leave it there and let the user
tidy up, which was rejected on the grounds that the list would slowly fill
with films the user had already seen, and every user would end up
performing the same cleanup by hand. A watchlist that needs maintenance
stops being useful long before it stops being correct.

The second alternative was to have the interface issue a second request
after recording a viewing. That was rejected for a different reason: the
rule would then live in the client, and anything calling the API directly
would produce data the application considers impossible, namely a film that
is both watched and still waiting to be watched. Rules that describe the
domain belong on the server, where there is only one of them.

The cost is a dependency from the watch log service to the watchlist
service, and a side effect that the name `POST /api/watch-logs` does not
advertise. Both are stated in the Javadoc of the operation. The dependency
runs in one direction only, and the two changes share a transaction, so
they either both happen or neither does.

## Demonstration data isolated from the migration chain

Two different kinds of data were living in the same migration chain.
Reference data such as roles, capabilities and their assignments is part of
the schema contract; the application cannot function without it.
Demonstration data such as the sample accounts is not.

Keeping them together meant that any deployment would create accounts whose
credentials are published in this repository. The demonstration data was
moved to `classpath:db/demo`, which only the development profile loads. The
default configuration loads only `classpath:db/migration`, so no other
environment ever receives it.

Demonstration migrations are numbered from V901 upward so that they can
never collide with future schema changes. A schema migration added later
therefore carries a lower version than demonstration data already applied,
which Flyway refuses by default, so out of order migrations are enabled in
the development profile. The log records each such migration as
[out of order]. The trade off is that a database built from scratch applies
the files in a different sequence than one migrated incrementally —
acceptable here because no migration depends on demonstration data.

## Three response shapes for a user profile

A user is returned through three different records, each carrying only what
its audience is allowed to see. `UserProfileResponse` comes back from
`/api/users/me` alone and includes the email address, the assigned role and
the capabilities it grants. `UserSummaryResponse` is what every other reader
sees, and omits all three. `UserAdminResponse` is the administration
listing: it carries the role and whether the account is enabled, because
administration is about exactly those, and it leaves the email address out
as well.

A single shared record would have exposed every address to any authenticated
caller, simply because one endpoint needed it. Three shapes cost two extra
records and two extra mapper methods, and remove the possibility of that
mistake rather than relying on remembering it.

The administrative shape is the one worth stating twice: an administrator
can change what an account may do without ever being shown how to contact
the person behind it. Nothing in the interface needs the address, so nothing
sends it.

## Viewings are counted in public, but never listed

A review is public one by one. It appears on the page of the film it
concerns, carrying the name of whoever wrote it, and the profile of that
person gathers the same reviews in one place. Nothing is revealed by the
gathering that the film pages did not already show.

Viewings are treated differently. A film says how many times it has been
watched, and a profile says how many films someone has seen, but no screen
anywhere says that a particular person watched a particular film on a
particular evening. The figures are public; the diary behind them is not.

The alternative was a public viewing history, which is how several film
journals work and which was considered while building the profile page.
It was rejected because it changes what the application promises rather
than what it displays. Every other private thing in Reverie is private by
the same rule: the watchlist is visible only to its owner, and the
statistics endpoint leaves it out deliberately. A public diary would make
viewings the single exception, and the exception would have to be
explained on every screen that touched it.

The cost is a number without a list behind it: a profile states that
someone has watched forty films and offers no way to see which. That is
accepted. Should the promise ever change, the change belongs in one place
— a new endpoint, guarded like any other — rather than in a page that
quietly started showing more than it used to.

## Administrators cannot change their own role

An administrator may change any account's role except their own. The
scenario this prevents is short: the only administrator demotes themselves,
and from that moment no account in the system can grant the role back. The
only remaining fix is editing the database by hand.

The request is refused with `409 Conflict` rather than `403 Forbidden`. The
caller does hold the `USER_UPDATE` capability, so this is not a permission
problem; the operation is refused because it conflicts with a rule that
protects the system, and it would be refused for every administrator alike.

A more precise rule would allow self demotion as long as another
administrator remains. That requires counting administrators on every call
and introduces a race between two administrators demoting themselves at the
same time. The simpler rule achieves the same protection at a fraction of
the complexity.

## Separate request records instead of entities

Endpoints accept purpose built records rather than the entities themselves.
An update request for a profile carries only the three fields a user may
change, so an email address or a role sent alongside them is discarded
before it reaches the service. Accepting the entity would have made every
column writable by anyone able to guess its name.

## An explicit page representation

Paginated endpoints return a `PageResponse`, a record defined by the
application, rather than Spring Data's own `Page`.

Spring Data documents that the serialised form of its page implementation
is not a stable API contract and may change between versions. It also
carries nested pagination objects that a client does not need. Defining the
response explicitly keeps the JSON shape small, documented and under the
application's control, which matters because a frontend is written against
it.

Pagination was applied selectively. Films, reviews and watch logs grow
without bound as the catalogue and each user's history expand. Genres are a
bounded set of roughly a dozen reference records, and the administrative
user listing is small and read by administrators only. Paginating those
would add client side complexity with nothing to gain.

## Public identifiers separate from database keys

Every entity has a numeric primary key used internally and a UUID exposed
through the API. Endpoints accept and return only the UUID.

Sequential integers leak information. A client that receives film number 47
learns roughly how many films exist and can walk through the others by
counting. A UUID reveals nothing and cannot be guessed.

The cost is an extra indexed column on every table and an additional lookup
by UUID rather than by primary key. For an application of this size the
trade is clearly worth it.

## A reader in prose, a user in the schema

The table, the entity, the endpoints and every parameter are called `user`.
Comments and interface text call the same person a reader.

The two words answer different questions. `users` is what the row is called,
and it stays that way because renaming it would touch a schema, an API and a
hundred parameter names for no gain. A reader is what the person is, and it
is already the word the interface uses when it says how you appear to other
readers. A film journal has readers the way a shop has customers rather than
records.

The rule is mechanical enough to apply without deciding each time: if the
word names a table, a class or a parameter it is `user`; if it describes the
human being it is a reader. `V907__seed_more_demo_users.sql` fills the users
table, and its comments explain what six readers disagree about.

One use is deliberately given up. Nothing in this project calls the person
reading the source code a reader, because that meaning would collide with the
other one inside the same files, and a word that means two things in one
codebase means neither.

## Stateless authentication with JWT

The API keeps no session state. Each request carries a signed token that
identifies the user, and the server verifies the signature rather than
consulting a session store.

The token holds the user's public UUID as its subject, their email address
and their granted authorities. It holds no password and nothing that would
be damaging if read, because the payload of a signed token is encoded
rather than encrypted: anyone holding the token can decode and read it
without any key. The signature guarantees that the contents were not
altered and that this server issued them, not that they are secret.

The authorities are written into the token but are never read back from it.
The authentication filter loads the account from the database on every
request instead. Carrying them costs a little space, and the redundancy is
deliberate: permissions read from the database are always current, so
changing a user's role takes effect immediately rather than whenever their
token happens to expire.

The subject is the public UUID, yet the account is looked up by the email
claim. The two are not symmetric. Nothing depends on this today because
email addresses cannot be changed, but the UUID is the stable identifier
and would be the better key if that ever changes.

There is no refresh token. A token expires one hour after it is issued and
the user signs in again. The frontend detects the resulting `401` in a
single interceptor and redirects to the login screen.

The known drawback of stateless tokens is that they cannot be revoked
before they expire. A disabled account would otherwise keep working until
its token ran out, so the authentication filter checks the account state on
every request instead of trusting the token alone.

## The database runs in a container

PostgreSQL runs from a Compose file rather than a local installation, so the
database is described in the repository and behaves identically on any
machine.

The image version is pinned rather than tracking `latest`. A moving tag
means that a future rebuild could silently pull a new major version and
change behaviour, which defeats the purpose of containerising it.

Data is stored in a named volume, so it survives stopping and recreating the
container. In practice this matters less than it might, because the entire
database is rebuilt from migrations in a couple of seconds.