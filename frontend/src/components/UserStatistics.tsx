import { useQuery } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import type { UserStatistics as Statistics } from '@/types/api'

/** Retrieves a summary of what a user has watched and written. */
async function fetchStatistics(uuid: string) {
    const { data } = await api.get<Statistics>(`/api/users/${uuid}/statistics`)
    return data
}

/**
 * Shows what a user has done, in numbers.
 *
 * The same panel serves a visitor looking at someone else and the owner
 * looking at themselves, because the figures cover public activity only.
 * What a user plans to watch is not among them.
 */
export function UserStatistics({ uuid }: { uuid: string }) {
    const { data, isPending, isError, error } = useQuery({
        queryKey: ['users', uuid, 'statistics'],
        queryFn: () => fetchStatistics(uuid),
    })

    if (isPending) {
        return <p className="text-sm text-muted-foreground">Loading activity…</p>
    }

    if (isError) {
        return (
            <p role="alert" className="text-sm text-destructive">
                {errorMessage(error)}
            </p>
        )
    }

    const figures = [
        { label: 'Films watched', value: String(data.filmsWatched) },
        { label: 'Viewings', value: String(data.viewingsRecorded) },
        { label: 'Reviews', value: String(data.reviewsWritten) },
        {
            label: 'Average rating',
            /* No average at all is different from an average of zero. */
            value: data.averageRating === null ? '—' : data.averageRating.toFixed(1),
        },
    ]

    return (
        <div className="space-y-3">
            <dl className="grid grid-cols-2 gap-3 sm:grid-cols-4">
                {figures.map((figure) => (
                    <div key={figure.label} className="rounded-lg border p-4">
                        <dt className="text-sm text-muted-foreground">{figure.label}</dt>
                        <dd className="mt-1 text-2xl font-semibold">{figure.value}</dd>
                    </div>
                ))}
            </dl>

            {data.favouriteGenre !== null && (
                <p className="text-sm text-muted-foreground">
                    Watches{' '}
                    <span className="font-medium text-foreground">
                        {data.favouriteGenre}
                    </span>{' '}
                    more than anything else.
                </p>
            )}
        </div>
    )
}