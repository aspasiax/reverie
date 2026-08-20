import { queryOptions } from '@tanstack/react-query'
import { api } from '@/lib/api'
import type { Genre } from '@/types/api'

/** Retrieves the genres a film may carry. */
async function fetchGenres() {
    const { data } = await api.get<Genre[]>('/api/genres')
    return data
}

/**
 * The active genres.
 *
 * Wanted in three unrelated places — the catalogue filters by them, the
 * film editor assigns them, the administration screen edits them — and all
 * three ask under the same key. Keeping one definition is what stops those
 * three from drifting into three different requests answering to one cache
 * entry.
 */
export const genresQuery = queryOptions({
    queryKey: ['genres'],
    queryFn: fetchGenres,
})