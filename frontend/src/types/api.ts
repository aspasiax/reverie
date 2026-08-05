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

export interface AuthResponse {
    accessToken: string
    tokenType: string
    expiresIn: number
}

export interface LoginRequest {
    email: string
    password: string
}

export interface GenreSummary {
    uuid: string
    name: string
    icon: string | null
    color: string | null
}

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
    genres: GenreSummary[]
    createdAt: string
    updatedAt: string
}

export interface UserProfile {
    uuid: string
    username: string
    email: string
    displayName: string
    bio: string | null
    profileImageUrl: string | null
    role: string
    createdAt: string
}

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