import { useEffect, useState } from 'react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { Link, useSearchParams } from 'react-router-dom'
import { Search, X } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Genre, Movie, MovieSort, Page } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
 * Returns a value only once it has stopped changing for a moment.
 *
 * Without this every keystroke would become a request. The delay is short
 * enough to feel immediate and long enough to skip the letters typed on
 * the way to a word.
 */
function useDebounced<T>(value: T, delay = 300): T {
    const [debounced, setDebounced] = useState(value)

    useEffect(() => {
        const timer = setTimeout(() => setDebounced(value), delay)
        return () => clearTimeout(timer)
    }, [value, delay])

    return debounced
}

/** Retrieves the genres offered as filters. */
async function fetchGenres() {
    const { data } = await api.get<Genre[]>('/api/genres')
    return data
}

/**
 * Retrieves one page of the film catalogue.
 *
 * @param page   the zero based page index
 * @param order  the order in which to return the page
 * @param search part of a title to match, empty for no filter
 * @param genre  the genre a film must carry, or null for no filter
 * @returns the requested page of films
 */
async function fetchMovies(
    page: number,
    order: MovieSort,
    search: string,
    genre: string | null,
) {
    const { data } = await api.get<Page<Movie>>('/api/movies', {
        params: { page, size: 12, order, search, genre },
    })
    return data
}

/**
 * The film catalogue.
 *
 * The order, the genre and the page live in the address rather than in
 * component state, so a narrowed catalogue can be linked to and the back
 * button behaves as expected. That is what lets a genre on a film page be
 * a link straight into this screen.
 *
 * The search term is deliberately not in the address. It is typing in
 * progress rather than a destination, and every pause would otherwise
 * leave an entry in the browser history.
 */
export function MoviesPage() {
    const [params, setParams] = useSearchParams()

    const requestedOrder = params.get('order')
    const order: MovieSort = orders.some((option) => option.value === requestedOrder)
        ? (requestedOrder as MovieSort)
        : 'TITLE'

    const genre = params.get('genre')
    const page = Number(params.get('page') ?? '0')

    const [search, setSearch] = useState('')
    const debouncedSearch = useDebounced(search)

    const { data: genres } = useQuery({
        queryKey: ['genres'],
        queryFn: fetchGenres,
    })

    const { data, isPending, isError, error } = useQuery({
        queryKey: ['movies', order, debouncedSearch, genre, page],
        queryFn: () => fetchMovies(page, order, debouncedSearch, genre),
        placeholderData: keepPreviousData,
    })

    /**
     * Writes a change into the address.
     *
     * A null value removes the parameter, so the address only ever carries
     * what has actually been chosen: the default order and the first page
     * leave no trace.
     */
    function updateParams(changes: Record<string, string | null>) {
        const next = new URLSearchParams(params)

        for (const [key, value] of Object.entries(changes)) {
            if (value === null) {
                next.delete(key)
            } else {
                next.set(key, value)
            }
        }

        setParams(next)
    }

    /*
     * Every change to a filter or to the order returns to the first page.
     * Page four of one selection has nothing to do with page four of
     * another, and someone who has just narrowed the catalogue is asking
     * to see what comes first.
     */
    function selectOrder(value: MovieSort) {
        updateParams({ order: value === 'TITLE' ? null : value, page: null })
    }

    function toggleGenre(uuid: string) {
        updateParams({ genre: genre === uuid ? null : uuid, page: null })
    }

    function changeSearch(value: string) {
        setSearch(value)

        if (page !== 0) {
            updateParams({ page: null })
        }
    }

    function goToPage(next: number) {
        updateParams({ page: next === 0 ? null : String(next) })
    }

    const hasFilters = search !== '' || genre !== null

    /** Clears both filters at once. */
    function clearFilters() {
        setSearch('')
        updateParams({ genre: null, page: null })
    }

    return (
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6">
            <div className="mb-4 space-y-3">
                <div className="flex flex-wrap items-center justify-between gap-3">
                    <div className="relative w-full sm:w-72">
                        <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                        <Input
                            type="search"
                            value={search}
                            onChange={(event) => changeSearch(event.target.value)}
                            placeholder="Search by title"
                            aria-label="Search films by title"
                            className="pl-9"
                        />
                    </div>

                    <div className="flex flex-wrap gap-1">
                        {orders.map((option) => (
                            <button
                                key={option.value}
                                type="button"
                                onClick={() => selectOrder(option.value)}
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

                <div className="flex flex-wrap items-center gap-1.5">
                    {genres?.map((option) => {
                        const isSelected = genre === option.uuid

                        return (
                            <button
                                key={option.uuid}
                                type="button"
                                onClick={() => toggleGenre(option.uuid)}
                                aria-pressed={isSelected}
                                className={cn(
                                    'inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-sm transition-colors',
                                    isSelected
                                        ? 'border-primary bg-accent font-medium text-accent-foreground'
                                        : 'text-muted-foreground hover:text-foreground',
                                )}
                            >
                                <span
                                    aria-hidden
                                    className="size-2 rounded-full"
                                    style={{ backgroundColor: option.color ?? 'transparent' }}
                                />
                                {option.name}
                            </button>
                        )
                    })}
                </div>
            </div>

            {isPending && <p className="text-muted-foreground">Loading films…</p>}

            {isError && (
                <p role="alert" className="text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {data !== undefined && (
                <>
                    <div className="mb-4 flex items-center gap-3">
                        <p className="text-sm text-muted-foreground">
                            {data.totalElements} films
                        </p>

                        {hasFilters && (
                            <Button variant="ghost" size="sm" onClick={clearFilters}>
                                <X className="size-4" />
                                Clear filters
                            </Button>
                        )}
                    </div>

                    {data.totalElements === 0 ? (
                        <div className="rounded-lg border border-dashed p-10 text-center">
                            <p className="font-medium">Nothing matches</p>
                            <p className="mt-1 text-sm text-muted-foreground">
                                Try a different title, or another genre.
                            </p>
                        </div>
                    ) : (
                        <>
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
                                    onClick={() => goToPage(page - 1)}
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
                                    onClick={() => goToPage(page + 1)}
                                    disabled={data.last}
                                >
                                    Next
                                </Button>
                            </div>
                        </>
                    )}
                </>
            )}
        </div>
    )
}