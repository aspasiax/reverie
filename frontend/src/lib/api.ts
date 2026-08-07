import axios, { AxiosError } from 'axios'
import type { ApiError } from '@/types/api'

/** Key under which the access token is kept in local storage. */
const TOKEN_KEY = 'reverie.token'

/** Returns the stored access token, or null when nobody is signed in. */
export const getToken = () => localStorage.getItem(TOKEN_KEY)

/** Stores the access token so it survives a page reload. */
export const setToken = (token: string) => localStorage.setItem(TOKEN_KEY, token)

/** Discards the stored access token, ending the session on this device. */
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

/**
 * The HTTP client used for every call to the Reverie API.
 *
 * The base address comes from the environment so that the same build can
 * point at a different server without touching the code.
 */
export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
    headers: { 'Content-Type': 'application/json' },
})

/*
 * Attaches the access token to every outgoing request. The token is read
 * on each call rather than captured once, so a fresh login takes effect
 * immediately.
 */
api.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

/*
 * The API answers 401 when a token is missing, expired or invalid. Since
 * tokens expire after an hour and there is no refresh token, this is the
 * normal end of a session: discard it and send the user back to the login
 * screen. A 403 is left alone, because there the user is known but simply
 * lacks the capability.
 */
api.interceptors.response.use(
    (response) => response,
    (error: AxiosError<ApiError>) => {
        if (error.response?.status === 401) {
            clearToken()
            if (window.location.pathname !== '/login') {
                window.location.href = '/login'
            }
        }
        return Promise.reject(error)
    },
)

/**
 * Extracts the message the API sent with an error response.
 *
 * The API answers failures with a consistent payload, so the message it
 * provides is almost always more useful than the generic text of the
 * underlying exception.
 *
 * @param error the value caught from a failed request
 * @returns a message suitable for showing to the user
 */
export function errorMessage(error: unknown): string {
    if (axios.isAxiosError<ApiError>(error)) {
        return error.response?.data?.message ?? 'Could not reach the server.'
    }
    return 'Something went wrong.'
}