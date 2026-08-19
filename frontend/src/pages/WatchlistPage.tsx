import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Bookmark, X } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Page, WatchlistEntry } from '@/types/api'
import { Button, buttonVariants } from '@/components/ui/button'
import { tmdbImage } from '@/lib/images'

/** Retrieves the films the signed in user intends to watch. */
async function fetchWatchlist() {
    const { data } = await api.get<Page<WatchlistEntry>>('/api/watchlist', {
        params: { page: 0, size: 100 },
    })
    return data
}

/**
 * The films the signed in user intends to watch, most recently added
 * first.
 *
 * Films leave this list in two ways: removed by hand, or recorded as
 * watched. The second happens on the server, so the list is invalidated
 * rather than edited here.
 */
export function WatchlistPage() {
    const queryClient = useQueryClient()

    const { data, isPending, isError, error } = useQuery({
        queryKey: ['watchlist'],
        queryFn: fetchWatchlist,
    })

    const remove = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/watchlist/${uuid}`),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watchlist'] })
        },
    })

    return (
        <div className="mx-auto max-w-7xl space-y-6 px-4 py-6 sm:px-6">
            <h1 className="text-2xl font-semibold">Watchlist</h1>

            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {remove.isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(remove.error)}
                </p>
            )}

            {data !== undefined && data.totalElements === 0 && (
                <div className="rounded-lg border border-dashed p-10 text-center">
                    <Bookmark className="mx-auto size-8 text-muted-foreground" />
                    <p className="mt-3 font-medium">Nothing here yet</p>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Films you add from a film page appear here, and leave it
                        once you log them as watched.
                    </p>
                    <Link
                        to="/"
                        className={buttonVariants({
                            variant: 'outline',
                            size: 'sm',
                            className: 'mt-4',
                        })}
                    >
                        Browse films
                    </Link>
                </div>
            )}

            {data !== undefined && data.totalElements > 0 && (
                <>
                    <p className="text-sm text-muted-foreground">
                        {data.totalElements} films to watch
                    </p>

                    <ul className="grid grid-cols-2 gap-x-4 gap-y-6 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6">
                        {data.content.map((item) => (
                            <li key={item.uuid} className="group relative">
                                <Link
                                    to={`/movies/${item.movieUuid}`}
                                    className="block space-y-2.5"
                                >
                                    <div className="relative aspect-[2/3] overflow-hidden rounded-lg bg-muted ring-1 ring-black/5 transition duration-200 group-hover:ring-2 group-hover:ring-primary/60 dark:ring-white/5">
                                        {tmdbImage(item.posterPath, 'w342') !== null ? (
                                            <img
                                                src={tmdbImage(item.posterPath, 'w342')!}
                                                alt=""
                                                loading="lazy"
                                                className="size-full object-cover transition-transform duration-300 group-hover:scale-105"
                                            />
                                        ) : (
                                            <div className="flex size-full items-center justify-center p-4 text-center text-sm text-muted-foreground">
                                                {item.movieTitle}
                                            </div>
                                        )}
                                    </div>

                                    <div className="space-y-0.5">
                                        <h2
                                            className="truncate text-sm font-medium leading-tight transition-colors group-hover:text-primary"
                                            title={item.movieTitle}
                                        >
                                            {item.movieTitle}
                                        </h2>
                                        <p className="text-xs text-muted-foreground">
                                            {item.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                                        </p>
                                    </div>
                                </Link>

                                <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => remove.mutate(item.uuid)}
                                    disabled={remove.isPending}
                                    aria-label={`Remove ${item.movieTitle} from your watchlist`}
                                    className="absolute right-2 top-2 opacity-0 shadow-lg transition-opacity group-hover:opacity-100 focus-visible:opacity-100"
                                >
                                    <X className="size-4" />
                                </Button>
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </div>
    )
}