import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Bookmark, BookmarkCheck } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { Page, WatchlistEntry } from '@/types/api'
import { Button } from '@/components/ui/button'

/** Retrieves the films the signed in user intends to watch. */
async function fetchWatchlist() {
    const { data } = await api.get<Page<WatchlistEntry>>('/api/watchlist', {
        params: { page: 0, size: 100 },
    })
    return data
}

/**
 * Adds a film to the watchlist, or takes it off again.
 *
 * Whether the film is already on the list is decided from the list itself
 * rather than from a dedicated request, which keeps this button working
 * from the same cache the watchlist screen uses. At the size this
 * application is built for the whole list arrives in one page.
 */
export function WatchlistButton({ movieUuid }: { movieUuid: string }) {
    const queryClient = useQueryClient()
    const { can } = useAuth()

    const { data: watchlist } = useQuery({
        queryKey: ['watchlist'],
        queryFn: fetchWatchlist,
        enabled: can('WATCHLIST_READ'),
    })

    const entry =
        watchlist?.content.find((item) => item.movieUuid === movieUuid) ?? null

    /** Refreshes the watchlist wherever it is shown. */
    function invalidateWatchlist() {
        queryClient.invalidateQueries({ queryKey: ['watchlist'] })
    }

    const add = useMutation({
        mutationFn: () => api.post('/api/watchlist', { movieUuid }),
        onSuccess: invalidateWatchlist,
    })

    const remove = useMutation({
        mutationFn: () => api.delete(`/api/watchlist/${entry!.uuid}`),
        onSuccess: invalidateWatchlist,
    })

    const failure = add.error ?? remove.error

    if (!can('WATCHLIST_CREATE')) {
        return null
    }

    return (
        <div className="space-y-1">
            <Button
                variant={entry === null ? 'outline' : 'secondary'}
                onClick={() => (entry === null ? add.mutate() : remove.mutate())}
                disabled={add.isPending || remove.isPending}
            >
                {entry === null ? (
                    <>
                        <Bookmark className="size-4" />
                        Add to watchlist
                    </>
                ) : (
                    <>
                        <BookmarkCheck className="size-4" />
                        On your watchlist
                    </>
                )}
            </Button>

            {failure !== null && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(failure)}
                </p>
            )}
        </div>
    )
}