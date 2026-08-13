import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { Clapperboard } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { errorMessage } from '@/lib/api'
import { tmdbImage } from '@/lib/images'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { TmdbNotice } from '@/components/TmdbAttribution'

/**
 * The film shown beside the sign in form.
 *
 * The path is written here rather than fetched, because this is the one
 * screen that exists before anyone has a token, and the catalogue is not
 * readable without one. It is a still from the demonstration data.
 */
const BACKDROP = '/5XNQBqnBwPA9yT0jZ0p3s8bbLh0.jpg'

/**
 * The sign in screen.
 *
 * The still occupies its own half of the screen rather than sitting behind
 * the form. Shown at full strength it is worth looking at, and the form
 * beside it never has to fight it for legibility.
 *
 * Authentication is handled through the auth context rather than a
 * mutation, because the result is not cached data but the identity of the
 * session itself. Everything else in the application depends on it.
 */
export function LoginPage() {
    const { login, isAuthenticated, isLoading } = useAuth()
    const navigate = useNavigate()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [isSubmitting, setIsSubmitting] = useState(false)

    /**
     * Signs the user in and sends them to the catalogue.
     *
     * The previous error is cleared before each attempt so that a failure
     * from an earlier submission does not linger next to a new one.
     */
    async function handleSubmit(event: FormEvent) {
        event.preventDefault()
        setError(null)
        setIsSubmitting(true)

        try {
            await login(email, password)
            navigate('/', { replace: true })
        } catch (caught) {
            setError(errorMessage(caught))
        } finally {
            setIsSubmitting(false)
        }
    }

    // Someone who is already signed in has no reason to see this screen.
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
                <div className="absolute inset-0 bg-gradient-to-t from-background via-background/20 to-transparent" />

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
                        <h1 className="text-2xl font-semibold tracking-tight">Sign in</h1>
                        <p className="text-sm text-muted-foreground">
                            Pick up where you left off.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(event) => setEmail(event.target.value)}
                                placeholder="user@reverie.com"
                                required
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
                                autoComplete="current-password"
                            />
                        </div>

                        {error !== null && (
                            <p
                                role="alert"
                                className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive"
                            >
                                {error}
                            </p>
                        )}

                        <Button type="submit" className="w-full" disabled={isSubmitting}>
                            {isSubmitting ? 'Signing in…' : 'Sign in'}
                        </Button>
                    </form>

                    <p className="text-sm text-muted-foreground">
                        New here?{' '}
                        <Link to="/register" className="underline hover:text-foreground">
                            Create an account
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    )
}