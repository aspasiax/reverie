import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { Clapperboard } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage, setToken } from '@/lib/api'
import { tmdbImage } from '@/lib/images'
import type { AuthResponse } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { TmdbNotice } from '@/components/TmdbAttribution'

/**
 * The film shown beside the form.
 *
 * The same still as the sign in screen, so that moving between the two
 * changes only the form and leaves the picture where it was.
 */
const BACKDROP = '/9udCLTxTFl28RxnK8Q05E154ZGa.jpg'

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
        <div className="grid min-h-screen lg:grid-cols-[3fr_2fr]">
            {/*
              * The still is hidden on narrow screens rather than shrunk. Half
              * a photograph beside a cramped form helps nobody.
              */}
            <div className="relative hidden lg:block">
                <img
                    src={tmdbImage(BACKDROP, 'w1280')!}
                    alt=""
                    className="absolute inset-0 size-full object-cover"
                />

                {/* Only enough shading for the wordmark to sit on. */}
                <div className="absolute inset-0 bg-gradient-to-t from-background via-background/40 to-transparent" />

                <div className="absolute inset-x-0 bottom-0 space-y-3 p-10">
                    <div className="flex items-center gap-2">
                        <Clapperboard className="size-6 text-primary" />
                        <span className="text-2xl font-semibold tracking-tight">
                            Reverie
                        </span>
                    </div>

                    <p className="max-w-sm text-sm leading-relaxed text-muted-foreground">
                        A journal for the films you watch. Keep a record of what you have
                        seen, write about it, and decide what comes next.
                    </p>

                    <TmdbNotice className="max-w-sm pt-1 opacity-70" />
                </div>
            </div>

            <div className="flex items-center justify-center p-6">
                <div className="w-full max-w-sm space-y-8">
                    {/* On narrow screens this stands in for the panel beside it. */}
                    <div className="flex items-center gap-2 lg:hidden">
                        <Clapperboard className="size-6 text-primary" />
                        <span className="text-2xl font-semibold tracking-tight">
                            Reverie
                        </span>
                    </div>

                    <div className="space-y-1.5">
                        <h1 className="text-2xl font-semibold tracking-tight">
                            Create an account
                        </h1>
                        <p className="text-sm text-muted-foreground">
                            Start keeping track of what you watch.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-5">
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
                                At least eight characters, with an upper and a lower case
                                letter, a digit and a symbol.
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

                        <Button
                            type="submit"
                            className="w-full"
                            disabled={create.isPending}
                        >
                            {create.isPending ? 'Creating…' : 'Create account'}
                        </Button>
                    </form>

                    <p className="text-sm text-muted-foreground">
                        Already have an account?{' '}
                        <Link to="/login" className="underline hover:text-foreground">
                            Sign in
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    )
}