import axios, { AxiosError } from 'axios'
import type { ApiError } from '@/types/api'

const TOKEN_KEY = 'reverie.token'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (token: string) => localStorage.setItem(TOKEN_KEY, token)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

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

/** Extracts the message the API sent, falling back to something readable. */
export function errorMessage(error: unknown): string {
    if (axios.isAxiosError<ApiError>(error)) {
        return error.response?.data?.message ?? 'Could not reach the server.'
    }
    return 'Something went wrong.'
}