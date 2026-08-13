import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { CalendarDays, Check, Eye, Pencil, Trash2, X } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Page, WatchLog } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'

/** Retrieves the viewing history of the authenticated user. */
async function fetchWatchLogs() {
    const { data } = await api.get<Page<WatchLog>>('/api/watch-logs', {
        params: { page: 0, size: 100 },
    })
    return data
}

/** Today in the local time zone, formatted as the date input expects. */
function todayLocal() {
    return new Date().toLocaleDateString('en-CA')
}

/**
 * The viewing controls of a film: logging a new viewing and reviewing the
 * ones already recorded.
 *
 * Both live behind dialogs so that the film page stays about the film. The
 * date is only asked for at the moment it is needed, and choosing not to
 * give one is a deliberate action rather than an empty field.
 */
export function WatchActions({ movieUuid }: { movieUuid: string }) {
    const queryClient = useQueryClient()

    const [isLogOpen, setIsLogOpen] = useState(false)
    const [isHistoryOpen, setIsHistoryOpen] = useState(false)
    const [watchedAt, setWatchedAt] = useState(todayLocal())
    const [editingUuid, setEditingUuid] = useState<string | null>(null)
    const [editingDate, setEditingDate] = useState('')

    const { data: watchLogs } = useQuery({
        queryKey: ['watch-logs'],
        queryFn: fetchWatchLogs,
    })

    /** The viewings of this film, newest first. */
    const viewings =
        watchLogs?.content.filter((log) => log.movieUuid === movieUuid) ?? []

    /*
     * The film is not checked against the existing history first: the domain
     * treats a rewatch as a separate entry, which is why watch logs carry no
     * uniqueness rule while reviews do.
     */
    const logWatch = useMutation({
        mutationFn: (date: string | null) =>
            api.post('/api/watch-logs', { movieUuid, watchedAt: date }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
            /*
             * Recording a viewing removes the film from the watchlist on
             * the server, so the cached list here is now out of date.
             */
            queryClient.invalidateQueries({ queryKey: ['watchlist'] })
            setIsLogOpen(false)
        },
    })

    const updateLog = useMutation({
        mutationFn: ({ uuid, date }: { uuid: string; date: string | null }) =>
            api.put(`/api/watch-logs/${uuid}`, { watchedAt: date }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
            setEditingUuid(null)
        },
    })

    const removeLog = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/watch-logs/${uuid}`),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
        },
    })

    /** Switches a viewing into edit mode with its current date filled in. */
    function startEditing(log: WatchLog) {
        setEditingUuid(log.uuid)
        setEditingDate(log.watchedAt ?? '')
        updateLog.reset()
    }

    /** Opens the dialog with today already filled in. */
    function openLogDialog() {
        setWatchedAt(todayLocal())
        logWatch.reset()
        setIsLogOpen(true)
    }

    return (
        <div className="flex flex-wrap items-center gap-2">
            <Button onClick={openLogDialog}>
                <Eye className="size-4" />
                Log as watched
            </Button>

            {viewings.length > 0 && (
                <Button variant="outline" onClick={() => setIsHistoryOpen(true)}>
                    <CalendarDays className="size-4" />
                    Watch history ({viewings.length})
                </Button>
            )}

            {/* Logging a viewing */}
            <Dialog open={isLogOpen} onOpenChange={setIsLogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Log this film as watched</DialogTitle>
                        <DialogDescription>
                            Watching a film again adds a second entry, it does not replace
                            the first.
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-2">
                        <Label htmlFor="watched-at">Date watched</Label>
                        <Input
                            id="watched-at"
                            type="date"
                            value={watchedAt}
                            max={todayLocal()}
                            onChange={(event) => setWatchedAt(event.target.value)}
                        />
                    </div>

                    {logWatch.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(logWatch.error)}
                        </p>
                    )}

                    <DialogFooter className="gap-2 sm:justify-between">
                        <Button
                            variant="ghost"
                            onClick={() => logWatch.mutate(null)}
                            disabled={logWatch.isPending}
                        >
                            I do not remember when
                        </Button>

                        <Button
                            onClick={() => logWatch.mutate(watchedAt)}
                            disabled={logWatch.isPending || watchedAt === ''}
                        >
                            {logWatch.isPending ? 'Saving…' : 'Save'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Viewings already recorded */}
            <Dialog open={isHistoryOpen} onOpenChange={setIsHistoryOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Your viewings</DialogTitle>
                        <DialogDescription>
                            {viewings.length === 1
                                ? 'You have watched this film once.'
                                : `You have watched this film ${viewings.length} times.`}
                        </DialogDescription>
                    </DialogHeader>

                    <ul className="space-y-2">
                        {viewings.map((log) => (
                            <li key={log.uuid} className="rounded-md border px-3 py-2">
                                {editingUuid === log.uuid ? (
                                    <div className="flex items-center gap-2">
                                        <Input
                                            type="date"
                                            value={editingDate}
                                            max={todayLocal()}
                                            onChange={(event) => setEditingDate(event.target.value)}
                                            className="h-8"
                                        />
                                        <Button
                                            size="sm"
                                            onClick={() =>
                                                updateLog.mutate({
                                                    uuid: log.uuid,
                                                    date: editingDate === '' ? null : editingDate,
                                                })
                                            }
                                            disabled={updateLog.isPending}
                                            aria-label="Save the date"
                                        >
                                            <Check className="size-4" />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => setEditingUuid(null)}
                                            aria-label="Cancel"
                                        >
                                            <X className="size-4" />
                                        </Button>
                                    </div>
                                ) : (
                                    <div className="flex items-center justify-between">
          <span className="text-sm">
            {log.watchedAt ?? 'Date not recorded'}
          </span>
                                        <div className="flex gap-1">
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => startEditing(log)}
                                                aria-label="Change the date"
                                            >
                                                <Pencil className="size-4" />
                                            </Button>
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => removeLog.mutate(log.uuid)}
                                                disabled={removeLog.isPending}
                                                aria-label="Remove this viewing"
                                            >
                                                <Trash2 className="size-4" />
                                            </Button>
                                        </div>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>

                    {updateLog.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(updateLog.error)}
                        </p>
                    )}

                    {removeLog.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(removeLog.error)}
                        </p>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}