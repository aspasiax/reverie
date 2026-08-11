import { useState } from 'react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { api, errorMessage } from '@/lib/api'
import type { Movie, MovieSort, Page } from '@/types/api'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { tmdbImage } from '@/lib/images'

/**
 * The orders offered to the reader.
 *
 * The values are sent to the API exactly as written, because the server
 * matches them against an enum and rejects anything else.
 */
const orders: { value: MovieSort; label: string }[] = [
    { value: 'TITLE', label: 'A–Z' },
    { value: 'MOST_WATCHED', label: 'Most watched' },
    { value: 'TOP_RATED', label: 'Top rated' },
]

/**
 * Retrieves one page of the film catalogue.
 *
 * @param page  the zero based page index
 * @param order the order in which to return the page
 * @returns the requested page of films
 */
async function fetchMovies(page: number, order: MovieSort) {
    const { data } = await api.get<Page<Movie>>('/api/movies', {
        params: { page, size: 12, order },
    })
    return data
}

/**
 * The film catalogue.
 *
 * The page index and the chosen order are both part of the query key, so
 * changing either is enough to make the data reload. Previously loaded
 * combinations stay in the cache and reappear instantly.
 */
export function MoviesPage() {
    const [page, setPage] = useState(0)
    const [order, setOrder] = useState<MovieSort>('TITLE')

    const { data, isPending, isError, error } = useQuery({
        queryKey: ['movies', order, page],
        queryFn: () => fetchMovies(page, order),
        placeholderData: keepPreviousData,
    })

    /**
     * Switches the order and returns to the first page.
     *
     * Page four of one ordering has nothing to do with page four of
     * another, and the reader is asking to see what comes first.
     */
    function changeOrder(next: MovieSort) {
        setOrder(next)
        setPage(0)
    }

    return (
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6">
            {isPending && <p className="text-muted-foreground">Loading films…</p>}

            {isError && (
                <p role="alert" className="text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {data !== undefined && (
                <>
                    <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
                        <p className="text-sm text-muted-foreground">
                            {data.totalElements} films
                        </p>

                        <div className="flex flex-wrap gap-1">
                            {orders.map((option) => (
                                <button
                                    key={option.value}
                                    type="button"
                                    onClick={() => changeOrder(option.value)}
                                    aria-pressed={order === option.value}
                                    className={cn(
                                        'rounded-md px-3 py-1.5 text-sm transition-colors',
                                        order === option.value
                                            ? 'bg-accent font-medium text-accent-foreground'
                                            : 'text-muted-foreground hover:text-foreground',
                                    )}
                                >
                                    {option.label}
                                </button>
                            ))}
                        </div>
                    </div>

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
        </div>
    )
}