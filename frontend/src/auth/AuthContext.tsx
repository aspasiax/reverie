import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { api, clearToken, getToken, setToken } from '@/lib/api'
import type { AuthResponse, UserProfile } from '@/types/api'
import type { Capability } from '@/lib/capabilities'

interface AuthContextValue {
    user: UserProfile | null
    isLoading: boolean
    isAuthenticated: boolean
    login: (email: string, password: string) => Promise<void>
    logout: () => void
    /** Reloads the profile after the user changes it. */
    refreshUser: () => Promise<void>
    /** Reports whether the signed in user holds the given capability. */
    can: (capability: Capability) => boolean
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

/**
 * Holds the authenticated user for the whole application.
 *
 * The token lives in local storage so a page reload does not sign the user
 * out, but the profile behind it is always fetched from the server. A token
 * that has expired or belongs to a disabled account is rejected on that
 * first request, which keeps the client from trusting stale credentials.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<UserProfile | null>(null)
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        if (!getToken()) {
            setIsLoading(false)
            return
        }

        api
            .get<UserProfile>('/api/users/me')
            .then((response) => setUser(response.data))
            .catch(() => {
                // The interceptor already discarded an invalid token.
                clearToken()
                setUser(null)
            })
            .finally(() => setIsLoading(false))
    }, [])

    const login = useCallback(async (email: string, password: string) => {
        const { data } = await api.post<AuthResponse>('/api/auth/login', {
            email,
            password,
        })

        setToken(data.accessToken)

        const profile = await api.get<UserProfile>('/api/users/me')
        setUser(profile.data)
    }, [])

    const logout = useCallback(() => {
        clearToken()
        setUser(null)
    }, [])

    const refreshUser = useCallback(async () => {
        const { data } = await api.get<UserProfile>('/api/users/me')
        setUser(data)
    }, [])

    const can = useCallback(
        (capability: Capability) => user?.capabilities.includes(capability) ?? false,
        [user],
    )

    return (
        <AuthContext.Provider
            value={{
                user,
                isLoading,
                isAuthenticated: user !== null,
                login,
                refreshUser,
                can,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

/** Reads the authentication state. Must be used inside AuthProvider. */
export function useAuth(): AuthContextValue {
    const context = useContext(AuthContext)

    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider')
    }

    return context
}