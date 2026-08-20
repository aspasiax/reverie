import { queryOptions } from '@tanstack/react-query'
import { api } from '@/lib/api'
import type { Page, WatchlistEntry } from '@/types/api'

/** Retrieves the films the authenticated user intends to watch. */
async function fetchWatchlist() {
    const { data } = await api.get<Page<WatchlistEntry>>('/api/watchlist', {
        params: { page: 0, size: 100 },
    })
    return data
}

/**
 * The films the authenticated user intends to watch.
 *
 * The button on a film page decides what it offers from this same list
 * rather than asking whether one film is on it, which is why both it and
 * the watchlist screen read one cache entry. At the size this application
 * is built for the whole list arrives in a single page.
 */
export const watchlistQuery = queryOptions({
    queryKey: ['watchlist'],
    queryFn: fetchWatchlist,
})