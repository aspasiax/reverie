import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { Clapperboard } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage, setToken } from '@/lib/api'
import type { AuthResponse } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface RegisterFields {
    username: string
    email: string
    password: string
    displayName: string
}

/**
 * Creates an account and returns the token issued for it.
 */
async function register(fields: RegisterFields) {
    const { data } = await api.post<AuthResponse>('/api/auth/register', fields)
    return data
}

/**
 * The sign up screen.
 *
 * Registration returns a token, so a new account is signed in immediately
 * rather than being sent back to the login screen to repeat the password
 * it has just chosen.
 */
export function RegisterPage() {
    const { isAuthenticated, isLoading, refreshUser } = useAuth()
    const navigate = useNavigate()

    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [displayName, setDisplayName] = useState('')

    const create = useMutation({
        mutationFn: () => register({ username, email, password, displayName }),
        onSuccess: async (data) => {
            setToken(data.accessToken)
            await refreshUser()
            navigate('/', { replace: true })
        },
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        create.mutate()
    }

    if (!isLoading && isAuthenticated) {
        return <Navigate to="/" replace />
    }

    return (
        <div className="flex min-h-screen items-center justify-center p-4">
            <form
                onSubmit={handleSubmit}
                className="w-full max-w-sm space-y-6 rounded-xl border bg-card p-8"
            >
                <div className="space-y-2 text-center">
                    <Clapperboard className="mx-auto size-8 text-primary" />
                    <h1 className="text-2xl font-semibold">Create an account</h1>
                    <p className="text-sm text-muted-foreground">
                        Start keeping track of what you watch
                    </p>
                </div>

                <div className="space-y-2">
                    <Label htmlFor="username">Username</Label>
                    <Input
                        id="username"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        required
                        minLength={3}
                        maxLength={50}
                        autoComplete="username"
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="display-name">Display name</Label>
                    <Input
                        id="display-name"
                        value={displayName}
                        onChange={(event) => setDisplayName(event.target.value)}
                        required
                        minLength={2}
                        maxLength={150}
                        autoComplete="name"
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        required
                        maxLength={255}
                        autoComplete="email"
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="password">Password</Label>
                    <Input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        required
                        minLength={8}
                        maxLength={100}
                        autoComplete="new-password"
                    />
                    <p className="text-xs text-muted-foreground">
                        At least eight characters, with an upper and a lower case letter,
                        a digit and a symbol.
                    </p>
                </div>

                {create.isError && (
                    <p
                        role="alert"
                        className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive"
                    >
                        {errorMessage(create.error)}
                    </p>
                )}

                <Button type="submit" className="w-full" disabled={create.isPending}>
                    {create.isPending ? 'Creating…' : 'Create account'}
                </Button>

                <p className="text-center text-sm text-muted-foreground">
                    Already have an account?{' '}
                    <Link to="/login" className="underline hover:text-foreground">
                        Sign in
                    </Link>
                </p>
            </form>
        </div>
    )
}