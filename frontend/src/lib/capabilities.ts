/**
 * The permissions the API grants through the role assigned to an account.
 *
 * Every protected endpoint checks one of these names, so listing them here
 * lets the interface hide controls the user would not be allowed to use.
 * The client only decides what to render: access itself is still decided by
 * the server on every request, and a hidden control is not a protected one.
 */
export type Capability =
    | 'MOVIE_READ'
    | 'MOVIE_CREATE'
    | 'MOVIE_UPDATE'
    | 'MOVIE_DELETE'
    | 'GENRE_READ'
    | 'GENRE_CREATE'
    | 'GENRE_UPDATE'
    | 'GENRE_DELETE'
    | 'REVIEW_READ'
    | 'REVIEW_CREATE'
    | 'REVIEW_UPDATE'
    | 'REVIEW_DELETE'
    | 'WATCH_LOG_READ'
    | 'WATCH_LOG_CREATE'
    | 'WATCH_LOG_UPDATE'
    | 'WATCH_LOG_DELETE'
    | 'USER_READ'
    | 'USER_CREATE'
    | 'USER_UPDATE'
    | 'USER_DELETE'

/**
 * The capabilities that unlock the administration area.
 *
 * Holding any one of them is enough to reach it, because each section
 * inside guards itself with the capability it actually needs.
 */
export const ADMIN_CAPABILITIES: Capability[] = [
    'MOVIE_CREATE',
    'GENRE_CREATE',
    'USER_READ',
]