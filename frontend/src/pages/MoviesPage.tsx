import { useState } from 'react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { LogOut } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page } from '@/types/api'
import { Button } from '@/components/ui/button'
import { tmdbImage } from '@/lib/images'
import {TmdbAttribution} from "@/components/TmdbAttribution.tsx";

/**
 * Retrieves one page of the film catalogue.
 *
 * @param page the zero based page index
 * @returns the requested page of films
 */
async function fetchMovies(page: number) {
    const { data } = await api.get<Page<Movie>>('/api/movies', {
        params: { page, size: 12 },
    })
    return data
}

/**
 * The film catalogue.
 *
 * The page index is part of the query key, so moving between pages is
 * enough to make the data reload. Previously loaded pages stay in the
 * cache and reappear instantly when the user goes back to them.
 */
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

                        <ul className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6">
                            {data.content.map((movie) => (
                                <li key={movie.uuid}>
                                    <Link
                                        to={`/movies/${movie.uuid}`}
                                        className="group block overflow-hidden rounded-lg border transition-colors hover:border-primary"
                                    >
                                        <div className="aspect-[2/3] overflow-hidden bg-muted">
                                            {tmdbImage(movie.posterPath, 'w342') !== null ? (
                                                <img
                                                    src={tmdbImage(movie.posterPath, 'w342')!}
                                                    alt=""
                                                    loading="lazy"
                                                    className="size-full object-cover transition-transform group-hover:scale-105"
                                                />
                                            ) : (
                                                <div className="flex size-full items-center justify-center p-4 text-center text-sm text-muted-foreground">
                                                    {movie.title}
                                                </div>
                                            )}
                                        </div>

                                        <div className="p-3">
                                            <h2 className="truncate font-medium" title={movie.title}>
                                                {movie.title}
                                            </h2>
                                            <p className="text-sm text-muted-foreground">
                                                {movie.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                                                {movie.runtime !== null && ` · ${movie.runtime} min`}
                                            </p>
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

            <TmdbAttribution />
        </div>
    )
}