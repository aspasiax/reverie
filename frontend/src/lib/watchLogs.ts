import { queryOptions } from '@tanstack/react-query'
import { api } from '@/lib/api'
import type { Page, WatchLog } from '@/types/api'

/** Retrieves the viewing history of the authenticated user. */
async function fetchWatchLogs() {
    const { data } = await api.get<Page<WatchLog>>('/api/watch-logs', {
        params: { page: 0, size: 100 },
    })
    return data
}

/**
 * The viewing history of the authenticated user.
 *
 * Shared rather than repeated. The film page and the profile form both want
 * the same history, and two components asking under the same key with
 * different requests would leave whichever asked second silently reading
 * the answer to the other's question.
 */
export const watchLogsQuery = queryOptions({
    queryKey: ['watch-logs'],
    queryFn: fetchWatchLogs,
})