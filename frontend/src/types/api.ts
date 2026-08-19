import type { Capability } from '@/lib/capabilities'

// Infrastructure

/** Matches ApiErrorResponse on the server. */
export interface ApiError {
    timestamp: string
    status: number
    error: string
    message: string
    path: string
}

/** Matches PageResponse<T> on the server. */
export interface Page<T> {
    content: T[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
}

// Authentication

/** Credentials submitted to the login endpoint. */
export interface LoginRequest {
    email: string
    password: string
}

/** The token returned after a successful sign in. */
export interface AuthResponse {
    accessToken: string
    tokenType: string
    expiresIn: number
}

// Users

/** A film as it appears embedded inside another response. */
export interface MovieSummary {
    uuid: string
    title: string
    releaseDate: string | null
    posterPath: string | null
}

/** The profile of the authenticated user, including private fields. */
export interface UserProfile {
    uuid: string
    username: string
    email: string
    displayName: string
    bio: string | null
    profileImageUrl: string | null
    favouriteMovie: MovieSummary | null
    role: string
    capabilities: Capability[]
    createdAt: string
}

/** A user account as an administrator sees it. */
export interface UserAdmin {
    uuid: string
    username: string
    displayName: string
    role: string
    enabled: boolean
    createdAt: string
}

/** The public view of another user. */
export interface UserSummary {
    uuid: string
    username: string
    displayName: string
    bio: string | null
    profileImageUrl: string | null
    favouriteMovie: MovieSummary | null
    createdAt: string
}

/** A summary of a user's public activity. */
export interface UserStatistics {
    filmsWatched: number
    viewingsRecorded: number
    reviewsWritten: number
    averageRating: number | null
    favouriteGenre: string | null
}

/** The profile fields a user may change. */
export interface UpdateUserRequest {
    displayName: string
    bio: string | null
    profileImageUrl: string | null
    favouriteMovieUuid: string | null
}

// Catalogue

/** A genre with every field the API exposes. */
export interface Genre {
    uuid: string
    name: string
    description: string | null
    icon: string | null
    color: string | null
    createdAt: string
    updatedAt: string
}

/** A genre badge attached to a film. */
export interface GenreSummary {
    uuid: string
    name: string
    icon: string | null
    color: string | null
}

/** The orders in which the catalogue can be browsed. */
export type MovieSort = 'TITLE' | 'MOST_WATCHED' | 'TOP_RATED'

/** A film in the catalogue, as returned by the API. */
export interface Movie {
    uuid: string
    title: string
    originalTitle: string | null
    overview: string | null
    releaseDate: string | null
    runtime: number | null
    originalLanguage: string | null
    posterPath: string | null
    backdropPath: string | null
    tmdbId: number | null
    imdbId: string | null
    published: boolean
    averageRating: number
    ratingCount: number
    watchCount: number
    genres: GenreSummary[]
    createdAt: string
    updatedAt: string
}

// Activity

/** A film the signed in user intends to watch. */
export interface WatchlistEntry {
    uuid: string
    movieUuid: string
    movieTitle: string
    releaseDate: string | null
    posterPath: string | null
    createdAt: string
}

/** A single recorded viewing in a user's watch history. */
export interface WatchLog {
    uuid: string
    movieUuid: string
    movieTitle: string
    posterPath: string | null
    watchedAt: string | null
    createdAt: string
}

/** A review written by a user for a film. */
export interface Review {
    uuid: string
    userUuid: string
    username: string
    displayName: string
    profileImageUrl: string | null
    movieUuid: string
    movieTitle: string
    posterPath: string | null
    rating: number | null
    reviewText: string | null
    createdAt: string
    updatedAt: string
}

// Statistics

/** A leading entry and the count behind it. */
export interface Highlight {
    name: string
    count: number
}

/** A summary of the whole application. */
export interface Overview {
    users: number
    disabledUsers: number
    publishedFilms: number
    unpublishedFilms: number
    deletedFilms: number
    genres: number
    reviews: number
    viewings: number
    watchlistEntries: number
    mostWatchedFilm: Highlight | null
    mostActiveUser: Highlight | null
}