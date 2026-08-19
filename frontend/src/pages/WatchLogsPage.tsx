import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Eye, Trash2 } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import { tmdbImage } from '@/lib/images'
import { dayLabel, monthLabel } from '@/lib/dates'
import { watchLogsQuery } from '@/lib/watchLogs'
import type { WatchLog } from '@/types/api'
import { Button, buttonVariants } from '@/components/ui/button'

/** One month of viewings, with the heading it is shown under. */
interface MonthGroup {
    key: string
    label: string
    logs: WatchLog[]
}

/**
 * Arranges viewings into the months they happened in, newest first.
 *
 * The server returns them in the order they were recorded, which is not the
 * order they happened: a film watched years ago and logged this morning
 * arrives at the top. A history is read by when things happened, so it is
 * rearranged here rather than in the query, which other screens share.
 *
 * A viewing with no date recorded belongs to no month and is gathered at
 * the end, where it can still be read and removed.
 *
 * @param logs the viewings as the server returned them
 * @return the months, each holding its viewings newest first
 */
function groupByMonth(logs: WatchLog[]): MonthGroup[] {
    const groups: MonthGroup[] = []

    const dated = logs
        .filter((log) => log.watchedAt !== null)
        .sort((first, second) => second.watchedAt!.localeCompare(first.watchedAt!))

    for (const log of dated) {
        const key = log.watchedAt!.slice(0, 7)
        const current = groups.at(-1)

        if (current !== undefined && current.key === key) {
            current.logs.push(log)
        } else {
            groups.push({ key, label: monthLabel(key), logs: [log] })
        }
    }

    const undated = logs.filter((log) => log.watchedAt === null)

    if (undated.length > 0) {
        groups.push({ key: 'undated', label: 'Date not recorded', logs: undated })
    }

    return groups
}

/**
 * The viewing history of the authenticated user, kept as a diary.
 *
 * The same film may appear more than once: the domain treats a rewatch as a
 * separate entry, which is why watch logs carry no uniqueness rule. That is
 * also why this is a list of dated rows rather than the shelf of posters the
 * watchlist uses — a watchlist holds each film once, a history holds every
 * time you saw it.
 */
export function WatchLogsPage() {
    const queryClient = useQueryClient()

    const { data, isPending, isError, error } = useQuery(watchLogsQuery)

    const removeLog = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/watch-logs/${uuid}`),
        /*
         * The list is invalidated rather than edited in place, so the count and
         * the ordering come back from the server instead of being recalculated
         * here.
         */
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
        },
    })

    const months = groupByMonth(data?.content ?? [])

    return (
        <div className="mx-auto max-w-3xl space-y-6 px-4 py-6 sm:px-6">
            <h1 className="text-2xl font-semibold">Watch history</h1>

            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {removeLog.isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(removeLog.error)}
                </p>
            )}

            {data !== undefined && data.totalElements === 0 && (
                <div className="rounded-lg border border-dashed p-10 text-center">
                    <Eye className="mx-auto size-8 text-muted-foreground" />
                    <p className="mt-3 font-medium">Nothing watched yet</p>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Open a film and log it as watched. Every viewing is kept, so
                        seeing something twice shows up twice.
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
                        {data.totalElements} viewings
                    </p>

                    <div className="space-y-8">
                        {months.map((month) => (
                            <section key={month.key} className="space-y-3">
                                <h2 className="text-sm font-medium tracking-wide text-muted-foreground uppercase">
                                    {month.label}
                                </h2>

                                <ul className="space-y-2">
                                    {month.logs.map((log) => (
                                        <li
                                            key={log.uuid}
                                            className="group flex items-center gap-4 rounded-lg border p-3 transition-colors hover:bg-accent/40"
                                        >
                                            <Link
                                                to={`/movies/${log.movieUuid}`}
                                                className="w-12 shrink-0"
                                            >
                                                <div className="aspect-[2/3] overflow-hidden rounded bg-muted">
                                                    {tmdbImage(log.posterPath, 'w185') !== null && (
                                                        <img
                                                            src={tmdbImage(log.posterPath, 'w185')!}
                                                            alt=""
                                                            loading="lazy"
                                                            className="size-full object-cover"
                                                        />
                                                    )}
                                                </div>
                                            </Link>

                                            <div className="min-w-0 flex-1">
                                                <Link
                                                    to={`/movies/${log.movieUuid}`}
                                                    className="block truncate font-medium transition-colors hover:text-primary"
                                                    title={log.movieTitle}
                                                >
                                                    {log.movieTitle}
                                                </Link>

                                                <p className="text-sm text-muted-foreground">
                                                    {log.watchedAt === null
                                                        ? 'No date recorded'
                                                        : dayLabel(log.watchedAt)}
                                                </p>
                                            </div>

                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => removeLog.mutate(log.uuid)}
                                                disabled={removeLog.isPending}
                                                aria-label={`Remove ${log.movieTitle} from history`}
                                                className="shrink-0 opacity-0 transition-opacity group-hover:opacity-100 focus-visible:opacity-100"
                                            >
                                                <Trash2 className="size-4" />
                                            </Button>
                                        </li>
                                    ))}
                                </ul>
                            </section>
                        ))}
                    </div>
                </>
            )}
        </div>
    )
}