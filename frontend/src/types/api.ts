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

/** The profile of the authenticated user, including private fields. */
export interface UserProfile {
    uuid: string
    username: string
    email: string
    displayName: string
    bio: string | null
    profileImageUrl: string | null
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

/** The profile fields a user may change. */
export interface UpdateUserRequest {
    displayName: string
    bio: string | null
    profileImageUrl: string | null
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
    movieUuid: string
    movieTitle: string
    posterPath: string | null
    rating: number | null
    reviewText: string | null
    createdAt: string
    updatedAt: string
}