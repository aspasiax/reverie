import { useQuery } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import type { Overview } from '@/types/api'

/** Retrieves the summary of the whole application. */
async function fetchOverview() {
    const { data } = await api.get<Overview>('/api/statistics')
    return data
}

/**
 * What the installation contains, at a glance.
 *
 * Unlike the statistics on a profile, this describes the application
 * rather than a person, which is why reading it needs its own capability.
 */
export function AdminOverviewPage() {
    const { data, isPending, isError, error } = useQuery({
        queryKey: ['statistics', 'overview'],
        queryFn: fetchOverview,
    })

    if (isPending) {
        return <p className="text-muted-foreground">Loading…</p>
    }

    if (isError) {
        return (
            <p role="alert" className="text-sm text-destructive">
                {errorMessage(error)}
            </p>
        )
    }

    const groups = [
        {
            title: 'Accounts',
            figures: [
                { label: 'Registered', value: data.users },
                { label: 'Disabled', value: data.disabledUsers },
            ],
        },
        {
            title: 'Catalogue',
            figures: [
                { label: 'Published', value: data.publishedFilms },
                { label: 'Drafts', value: data.unpublishedFilms },
                { label: 'Deleted', value: data.deletedFilms },
                { label: 'Genres', value: data.genres },
            ],
        },
        {
            title: 'Activity',
            figures: [
                { label: 'Viewings', value: data.viewings },
                { label: 'Reviews', value: data.reviews },
                { label: 'On watchlists', value: data.watchlistEntries },
            ],
        },
    ]

    return (
        <div className="space-y-6">
            {groups.map((group) => (
                <section key={group.title} className="space-y-2">
                    <h2 className="text-sm font-medium text-muted-foreground">
                        {group.title}
                    </h2>

                    <dl className="grid grid-cols-2 gap-3 sm:grid-cols-4">
                        {group.figures.map((figure) => (
                            <div key={figure.label} className="rounded-lg border p-4">
                                <dt className="text-sm text-muted-foreground">
                                    {figure.label}
                                </dt>
                                <dd className="mt-1 text-2xl font-semibold">
                                    {figure.value}
                                </dd>
                            </div>
                        ))}
                    </dl>
                </section>
            ))}

            {(data.mostWatchedFilm !== null || data.mostActiveUser !== null) && (
                <section className="space-y-2">
                    <h2 className="text-sm font-medium text-muted-foreground">
                        Leading
                    </h2>

                    <div className="space-y-1 rounded-lg border p-4 text-sm">
                        {data.mostWatchedFilm !== null && (
                            <p>
                                Most watched film:{' '}
                                <span className="font-medium">
                                    {data.mostWatchedFilm.name}
                                </span>{' '}
                                <span className="text-muted-foreground">
                                    ({data.mostWatchedFilm.count} viewings)
                                </span>
                            </p>
                        )}

                        {data.mostActiveUser !== null && (
                            <p>
                                Most active account:{' '}
                                <span className="font-medium">
                                    {data.mostActiveUser.name}
                                </span>{' '}
                                <span className="text-muted-foreground">
                                    ({data.mostActiveUser.count} viewings)
                                </span>
                            </p>
                        )}
                    </div>
                </section>
            )}
        </div>
    )
}