import { useState } from 'react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { LogOut } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page } from '@/types/api'
import { Button } from '@/components/ui/button'

async function fetchMovies(page: number) {
    const { data } = await api.get<Page<Movie>>('/api/movies', {
        params: { page, size: 12 },
    })
    return data
}

export function MoviesPage() {
    const { user, logout } = useAuth()

    const [page, setPage] = useState(0)
    const { data, isPending, isError, error } = useQuery({
        queryKey: ['movies', page],
        queryFn: () => fetchMovies(page),
        placeholderData: keepPreviousData,
    })

    return (
        <div className="min-h-screen">
            <header className="flex items-center justify-between border-b px-6 py-4">
                <h1 className="text-xl font-semibold">Reverie</h1>
                <div className="flex items-center gap-4">
                    <Link to="/watch-logs" className="text-sm hover:underline">
                        History
                    </Link>
          <span className="text-sm text-muted-foreground">
            {user?.displayName}
          </span>
                    <Button variant="ghost" size="sm" onClick={logout}>
                        <LogOut className="size-4" />
                        Sign out
                    </Button>
                </div>
            </header>

            <main className="p-6">
                {isPending && <p className="text-muted-foreground">Loading films…</p>}

                {isError && (
                    <p role="alert" className="text-destructive">
                        {errorMessage(error)}
                    </p>
                )}

                {data !== undefined && (
                    <>
                        <p className="mb-4 text-sm text-muted-foreground">
                            {data.totalElements} films
                        </p>

                        <ul className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                            {data.content.map((movie) => (
                                <li key={movie.uuid}>
                                    <Link
                                        to={`/movies/${movie.uuid}`}
                                        className="block rounded-lg border p-4 transition-colors hover:bg-accent"
                                    >
                                        <h2 className="font-medium">{movie.title}</h2>
                                        <p className="text-sm text-muted-foreground">
                                            {movie.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                                            {movie.runtime !== null && ` · ${movie.runtime} min`}
                                        </p>
                                        <div className="mt-2 flex flex-wrap gap-1">
                                            {movie.genres.map((genre) => (
                                                <span
                                                    key={genre.uuid}
                                                    className="rounded-full border px-2 py-0.5 text-xs text-muted-foreground"
                                                >
                          {genre.name}
                        </span>
                                            ))}
                                        </div>
                                    </Link>
                                </li>
                            ))}
                        </ul>

                        <div className="mt-6 flex items-center justify-center gap-4">
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => setPage((current) => current - 1)}
                                disabled={data.first}
                            >
                                Previous
                            </Button>

                            <span className="text-sm text-muted-foreground">
                                Page {data.page + 1} of {data.totalPages}
                              </span>

                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => setPage((current) => current + 1)}
                                disabled={data.last}
                            >
                                Next
                            </Button>
                        </div>
                    </>
                )}
            </main>
        </div>
    )
}