import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Pencil, Plus, Trash2, Undo2 } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { Genre } from '@/types/api'
import { Button } from '@/components/ui/button'
import { GenreEditor } from '@/components/GenreEditor'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'

/** Retrieves every active genre, ordered alphabetically by the server. */
async function fetchGenres() {
    const { data } = await api.get<Genre[]>('/api/genres')
    return data
}

/** Retrieves the genres that were soft deleted and can be restored. */
async function fetchDeletedGenres() {
    const { data } = await api.get<Genre[]>('/api/genres/deleted')
    return data
}

/**
 * Lets an administrator manage the genres of the catalogue.
 *
 * Deleting a genre only withdraws it from the catalogue, so it is done
 * without confirmation and can be undone from the list of deleted genres.
 * Destroying one is irreversible and is therefore confirmed first.
 */
export function AdminGenresPage() {
    const queryClient = useQueryClient()
    const { can } = useAuth()

    const [editing, setEditing] = useState<Genre | null>(null)
    const [isEditorOpen, setIsEditorOpen] = useState(false)
    const [confirming, setConfirming] = useState<Genre | null>(null)

    const { data: genres, isPending, isError, error } = useQuery({
        queryKey: ['genres'],
        queryFn: fetchGenres,
    })

    /*
     * The key starts with 'genres', so invalidating that prefix refreshes
     * this listing together with the active one: a genre always leaves one
     * of the two lists exactly when it enters the other.
     */
    const { data: deletedGenres } = useQuery({
        queryKey: ['genres', 'deleted'],
        queryFn: fetchDeletedGenres,
        enabled: can('GENRE_UPDATE'),
    })

    /** Refreshes every list in which a genre change is visible. */
    function invalidateGenres() {
        /*
         * Films carry their genres inside their own response, so a changed
         * genre would keep showing its old badge until those queries were
         * refetched on their own.
         */
        queryClient.invalidateQueries({ queryKey: ['genres'] })
        queryClient.invalidateQueries({ queryKey: ['movies'] })
        queryClient.invalidateQueries({ queryKey: ['movie'] })
    }

    const remove = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/genres/${uuid}`),
        onSuccess: invalidateGenres,
    })

    const restore = useMutation({
        mutationFn: (uuid: string) => api.post(`/api/genres/${uuid}/restore`),
        onSuccess: invalidateGenres,
    })

    const purge = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/genres/${uuid}/permanent`),
        onSuccess: () => {
            invalidateGenres()
            setConfirming(null)
        },
    })

    /** Opens the editor for an existing genre, or for a new one when given null. */
    function openEditor(genre: Genre | null) {
        setEditing(genre)
        setIsEditorOpen(true)
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">
                    {genres === undefined ? '' : `${genres.length} active genres`}
                </p>

                {can('GENRE_CREATE') && (
                    <Button size="sm" onClick={() => openEditor(null)}>
                        <Plus className="size-4" />
                        New genre
                    </Button>
                )}
            </div>

            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {restore.isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(restore.error)}
                </p>
            )}

            <ul className="divide-y rounded-lg border">
                {genres?.map((genre) => (
                    <li key={genre.uuid} className="flex items-center gap-3 p-3">
                        <span
                            aria-hidden
                            className="size-4 shrink-0 rounded-full border"
                            style={{ backgroundColor: genre.color ?? 'transparent' }}
                        />

                        <div className="min-w-0 flex-1">
                            <p className="truncate font-medium">{genre.name}</p>
                            {genre.description !== null && (
                                <p className="truncate text-sm text-muted-foreground">
                                    {genre.description}
                                </p>
                            )}
                        </div>

                        <span className="hidden shrink-0 text-xs text-muted-foreground sm:inline">
                            {genre.icon}
                        </span>

                        {can('GENRE_UPDATE') && (
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => openEditor(genre)}
                                aria-label={`Edit ${genre.name}`}
                            >
                                <Pencil className="size-4" />
                            </Button>
                        )}

                        {can('GENRE_DELETE') && (
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => remove.mutate(genre.uuid)}
                                disabled={remove.isPending}
                                aria-label={`Delete ${genre.name}`}
                            >
                                <Trash2 className="size-4" />
                            </Button>
                        )}
                    </li>
                ))}
            </ul>

            {deletedGenres !== undefined && deletedGenres.length > 0 && (
                <section className="space-y-2">
                    <h2 className="text-sm font-medium text-muted-foreground">
                        Deleted ({deletedGenres.length})
                    </h2>

                    <ul className="divide-y rounded-lg border border-dashed">
                        {deletedGenres.map((genre) => (
                            <li key={genre.uuid} className="flex items-center gap-3 p-3">
                                <span
                                    aria-hidden
                                    className="size-4 shrink-0 rounded-full border opacity-40"
                                    style={{ backgroundColor: genre.color ?? 'transparent' }}
                                />

                                <p className="min-w-0 flex-1 truncate text-muted-foreground">
                                    {genre.name}
                                </p>

                                <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => restore.mutate(genre.uuid)}
                                    disabled={restore.isPending}
                                    aria-label={`Restore ${genre.name}`}
                                >
                                    <Undo2 className="size-4" />
                                </Button>

                                {can('GENRE_DELETE') && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => setConfirming(genre)}
                                        aria-label={`Permanently delete ${genre.name}`}
                                    >
                                        <Trash2 className="size-4 text-destructive" />
                                    </Button>
                                )}
                            </li>
                        ))}
                    </ul>
                </section>
            )}

            {/*
             * The key remounts the editor whenever a different genre is chosen,
             * which is what resets the form fields to that genre's values.
             */}
            <GenreEditor
                key={editing?.uuid ?? 'new'}
                open={isEditorOpen}
                onOpenChange={setIsEditorOpen}
                existing={editing}
            />

            <Dialog
                open={confirming !== null}
                onOpenChange={(open) => {
                    if (!open) {
                        setConfirming(null)
                    }
                }}
            >
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Destroy “{confirming?.name}”?</DialogTitle>
                        <DialogDescription>
                            The genre is removed from the database for good, together
                            with its links to every film that carried it. This cannot
                            be undone.
                        </DialogDescription>
                    </DialogHeader>

                    {purge.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(purge.error)}
                        </p>
                    )}

                    <DialogFooter>
                        <Button variant="ghost" onClick={() => setConfirming(null)}>
                            Cancel
                        </Button>
                        <Button
                            variant="destructive"
                            onClick={() => purge.mutate(confirming!.uuid)}
                            disabled={purge.isPending}
                        >
                            {purge.isPending ? 'Deleting…' : 'Delete permanently'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}