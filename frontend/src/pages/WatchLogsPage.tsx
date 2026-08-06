import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowLeft, Trash2 } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Page, WatchLog } from '@/types/api'
import { Button } from '@/components/ui/button'

async function fetchWatchLogs() {
    const { data } = await api.get<Page<WatchLog>>('/api/watch-logs', {
        params: { page: 0, size: 50 },
    })
    return data
}

async function deleteWatchLog(uuid: string) {
    await api.delete(`/api/watch-logs/${uuid}`)
}

/**
 * Shows the viewing history of the authenticated user.
 *
 * The same film may appear more than once: the domain treats a rewatch as
 * a separate entry, which is why watch logs carry no uniqueness rule.
 */
export function WatchLogsPage() {
    const queryClient = useQueryClient()

    const { data, isPending, isError, error } = useQuery({
        queryKey: ['watch-logs'],
        queryFn: fetchWatchLogs,
    })

    const removeLog = useMutation({
        mutationFn: deleteWatchLog,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
        },
    })

    return (
        <div className="min-h-screen">
            <header className="border-b px-6 py-4">
                <Link
                    to="/"
                    className="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground"
                >
                    <ArrowLeft className="size-4" />
                    Back to films
                </Link>
            </header>

            <main className="mx-auto max-w-3xl space-y-6 p-6">
                <h1 className="text-2xl font-semibold">Watch history</h1>

                {isPending && <p className="text-muted-foreground">Loading…</p>}

                {isError && (
                    <p role="alert" className="text-destructive">
                        {errorMessage(error)}
                    </p>
                )}

                {data !== undefined && data.content.length === 0 && (
                    <p className="text-muted-foreground">
                        Nothing here yet. Open a film and log it as watched.
                    </p>
                )}

                {data !== undefined && data.content.length > 0 && (
                    <>
                        <p className="text-sm text-muted-foreground">
                            {data.totalElements} viewings
                        </p>

                        <ul className="space-y-2">
                            {data.content.map((log) => (
                                <li
                                    key={log.uuid}
                                    className="flex items-center justify-between rounded-lg border p-4"
                                >
                                    <div>
                                        <Link
                                            to={`/movies/${log.movieUuid}`}
                                            className="font-medium hover:underline"
                                        >
                                            {log.movieTitle}
                                        </Link>
                                        <p className="text-sm text-muted-foreground">
                                            {log.watchedAt ?? 'Date not recorded'}
                                        </p>
                                    </div>

                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => removeLog.mutate(log.uuid)}
                                        disabled={removeLog.isPending}
                                        aria-label={`Remove ${log.movieTitle} from history`}
                                    >
                                        <Trash2 className="size-4" />
                                    </Button>
                                </li>
                            ))}
                        </ul>
                    </>
                )}
            </main>
        </div>
    )
}