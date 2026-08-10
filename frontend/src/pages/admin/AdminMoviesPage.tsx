import { useState } from 'react'
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Eye, EyeOff, Pencil, Plus, Trash2, Undo2 } from 'lucide-react'
import { MovieEditor } from '@/components/MovieEditor'
import { useAuth } from '@/auth/AuthContext'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page } from '@/types/api'
import { Button } from '@/components/ui/button'
import { tmdbImage } from '@/lib/images'

/** Retrieves one page of every film that has not been deleted. */
async function fetchManagedMovies(page: number) {
    const { data } = await api.get<Page<Movie>>('/api/movies/all', {
        params: { page, size: 20 },
    })
    return data
}

/**
 * Retrieves the films that were soft deleted.
 *
 * These are fetched as a single page because the list is not expected to
 * grow: everything in it is waiting to be either restored or forgotten.
 */
async function fetchDeletedMovies() {
    const { data } = await api.get<Page<Movie>>('/api/movies/deleted', {
        params: { page: 0, size: 50 },
    })
    return data
}

/**
 * Lets an administrator manage the film catalogue.
 *
 * Unlike the public catalogue, this listing also shows films that are not
 * published yet, which is the only place they can be reached from.
 */
export function AdminMoviesPage() {
    const queryClient = useQueryClient()
    const { can } = useAuth()

    const [page, setPage] = useState(0)

    const [editing, setEditing] = useState<Movie | null>(null)
    const [isEditorOpen, setIsEditorOpen] = useState(false)

    const { data, isPending, isError, error } = useQuery({
        queryKey: ['movies', 'manage', page],
        queryFn: () => fetchManagedMovies(page),
        placeholderData: keepPreviousData,
    })

    /*
     * Both keys start with 'movies', so invalidating that prefix refreshes
     * this listing, the recycle bin and the public catalogue at once.
     */
    const { data: deleted } = useQuery({
        queryKey: ['movies', 'deleted'],
        queryFn: fetchDeletedMovies,
    })

    /** Refreshes every list in which a change to a film is visible. */
    function invalidateMovies() {
        queryClient.invalidateQueries({ queryKey: ['movies'] })
        queryClient.invalidateQueries({ queryKey: ['movie'] })
    }

    /*
     * Publishing and withdrawing are the same decision seen from two sides,
     * so they share one mutation and differ only in the address they call.
     */
    const setPublished = useMutation({
        mutationFn: ({ uuid, published }: { uuid: string; published: boolean }) =>
            api.post(`/api/movies/${uuid}/${published ? 'publish' : 'unpublish'}`),
        onSuccess: invalidateMovies,
    })

    const remove = useMutation({
        mutationFn: (uuid: string) => api.delete(`/api/movies/${uuid}`),
        onSuccess: invalidateMovies,
    })

    const restore = useMutation({
        mutationFn: (uuid: string) => api.post(`/api/movies/${uuid}/restore`),
        onSuccess: invalidateMovies,
    })

    /** Opens the editor for an existing film, or for a new one when given null. */
    function openEditor(movie: Movie | null) {
        setEditing(movie)
        setIsEditorOpen(true)
    }

    const failure = setPublished.error ?? remove.error ?? restore.error

    return (
        <div className="space-y-4">
            {isPending && <p className="text-muted-foreground">Loading films…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {failure !== null && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(failure)}
                </p>
            )}

            {data !== undefined && (
                <>
                    <div className="flex items-center justify-between">
                        <p className="text-sm text-muted-foreground">
                            {data.totalElements} films in the catalogue
                        </p>

                        {can('MOVIE_CREATE') && (
                            <Button size="sm" onClick={() => openEditor(null)}>
                                <Plus className="size-4" />
                                New film
                            </Button>
                        )}
                    </div>

                    <ul className="divide-y rounded-lg border">
                        {data.content.map((movie) => (
                            <li key={movie.uuid} className="flex items-center gap-3 p-3">
                                <div className="h-16 w-11 shrink-0 overflow-hidden rounded bg-muted">
                                    {tmdbImage(movie.posterPath, 'w92') !== null && (
                                        <img
                                            src={tmdbImage(movie.posterPath, 'w92')!}
                                            alt=""
                                            loading="lazy"
                                            className="size-full object-cover"
                                        />
                                    )}
                                </div>

                                <div className="min-w-0 flex-1">
                                    <p className="truncate font-medium">{movie.title}</p>
                                    <p className="text-sm text-muted-foreground">
                                        {movie.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                                        {movie.runtime !== null && ` · ${movie.runtime} min`}
                                    </p>
                                </div>

                                {!movie.published && (
                                    <span className="shrink-0 rounded-full border border-dashed px-2 py-0.5 text-xs text-muted-foreground">
                                        Draft
                                    </span>
                                )}

                                {can('MOVIE_UPDATE') && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => openEditor(movie)}
                                        aria-label={`Edit ${movie.title}`}
                                    >
                                        <Pencil className="size-4" />
                                    </Button>
                                )}

                                {can('MOVIE_UPDATE') && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={() =>
                                            setPublished.mutate({
                                                uuid: movie.uuid,
                                                published: !movie.published,
                                            })
                                        }
                                        disabled={setPublished.isPending}
                                        aria-label={
                                            movie.published
                                                ? `Withdraw ${movie.title} from the catalogue`
                                                : `Publish ${movie.title}`
                                        }
                                    >
                                        {movie.published ? (
                                            <Eye className="size-4" />
                                        ) : (
                                            <EyeOff className="size-4 text-muted-foreground" />
                                        )}
                                    </Button>
                                )}

                                {can('MOVIE_DELETE') && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => remove.mutate(movie.uuid)}
                                        disabled={remove.isPending}
                                        aria-label={`Delete ${movie.title}`}
                                    >
                                        <Trash2 className="size-4" />
                                    </Button>
                                )}
                            </li>
                        ))}
                    </ul>

                    <div className="flex items-center justify-center gap-4">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setPage((current) => current - 1)}
                            disabled={data.first}
                        >
                            Previous
                        </Button>

                        <span className="text-sm text-muted-foreground">
                            Page {data.page + 1} of {data.totalPages}
                        </span>

                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setPage((current) => current + 1)}
                            disabled={data.last}
                        >
                            Next
                        </Button>
                    </div>
                </>
            )}

            {deleted !== undefined && deleted.totalElements > 0 && (
                <section className="space-y-2 pt-2">
                    <h2 className="text-sm font-medium text-muted-foreground">
                        Deleted ({deleted.totalElements})
                    </h2>

                    <ul className="divide-y rounded-lg border border-dashed">
                        {deleted.content.map((movie) => (
                            <li key={movie.uuid} className="flex items-center gap-3 p-3">
                                <p className="min-w-0 flex-1 truncate text-muted-foreground">
                                    {movie.title}
                                    <span className="ml-2 text-sm">
                                        {movie.releaseDate?.slice(0, 4) ?? ''}
                                    </span>
                                </p>

                                <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => restore.mutate(movie.uuid)}
                                    disabled={restore.isPending}
                                    aria-label={`Restore ${movie.title}`}
                                >
                                    <Undo2 className="size-4" />
                                </Button>
                            </li>
                        ))}
                    </ul>
                </section>
            )}

            {/*
             * The key remounts the editor whenever a different film is chosen,
             * which is what resets the form fields to that film's values.
             */}
            <MovieEditor
                key={editing?.uuid ?? 'new'}
                open={isEditorOpen}
                onOpenChange={setIsEditorOpen}
                existing={editing}
            />
        </div>
    )
}