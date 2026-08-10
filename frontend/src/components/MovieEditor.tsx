import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import type { Genre, Movie } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'

interface MovieEditorProps {
    /** Whether the dialog is currently shown. */
    open: boolean
    /** Reports a request to open or close the dialog. */
    onOpenChange: (open: boolean) => void
    /** The film being changed, or null when creating a new one. */
    existing: Movie | null
}

/** Retrieves the genres offered for selection. */
async function fetchGenres() {
    const { data } = await api.get<Genre[]>('/api/genres')
    return data
}

/** Turns a blank field into null, which is what the API expects for absent values. */
function optional(value: string) {
    const trimmed = value.trim()
    return trimmed === '' ? null : trimmed
}

/**
 * Turns a numeric field into a number or null.
 *
 * The API rejects zero and negative values, so a blank field has to become
 * null rather than the zero that an empty numeric input would suggest.
 */
function optionalNumber(value: string) {
    const trimmed = value.trim()
    return trimmed === '' ? null : Number(trimmed)
}

/**
 * Creates a new film or changes an existing one.
 *
 * The same form serves both operations because the two requests carry
 * identical fields. A film is created unpublished unless the box is ticked,
 * which is what makes it possible to prepare an entry before readers see it.
 */
export function MovieEditor({ open, onOpenChange, existing }: MovieEditorProps) {
    const queryClient = useQueryClient()

    const [title, setTitle] = useState(existing?.title ?? '')
    const [originalTitle, setOriginalTitle] = useState(existing?.originalTitle ?? '')
    const [overview, setOverview] = useState(existing?.overview ?? '')
    const [releaseDate, setReleaseDate] = useState(existing?.releaseDate ?? '')
    const [runtime, setRuntime] = useState(existing?.runtime?.toString() ?? '')
    const [language, setLanguage] = useState(existing?.originalLanguage ?? '')
    const [posterPath, setPosterPath] = useState(existing?.posterPath ?? '')
    const [backdropPath, setBackdropPath] = useState(existing?.backdropPath ?? '')
    const [tmdbId, setTmdbId] = useState(existing?.tmdbId?.toString() ?? '')
    const [imdbId, setImdbId] = useState(existing?.imdbId ?? '')
    const [published, setPublished] = useState(existing?.published ?? false)
    const [genreUuids, setGenreUuids] = useState<string[]>(
        existing?.genres.map((genre) => genre.uuid) ?? [],
    )

    const { data: genres } = useQuery({
        queryKey: ['genres'],
        queryFn: fetchGenres,
    })

    const save = useMutation({
        mutationFn: () => {
            const body = {
                title: title.trim(),
                originalTitle: optional(originalTitle),
                overview: optional(overview),
                releaseDate: optional(releaseDate),
                runtime: optionalNumber(runtime),
                originalLanguage: optional(language),
                posterPath: optional(posterPath),
                backdropPath: optional(backdropPath),
                tmdbId: optionalNumber(tmdbId),
                imdbId: optional(imdbId),
                published,
                genreUuids,
            }

            return existing === null
                ? api.post('/api/movies', body)
                : api.put(`/api/movies/${existing.uuid}`, body)
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['movies'] })
            queryClient.invalidateQueries({ queryKey: ['movie'] })
            onOpenChange(false)
        },
    })

    /** Adds or removes a genre from the selection. */
    function toggleGenre(uuid: string) {
        setGenreUuids((current) =>
            current.includes(uuid)
                ? current.filter((selected) => selected !== uuid)
                : [...current, uuid],
        )
    }

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        save.mutate()
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-2xl">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <DialogHeader>
                        <DialogTitle>
                            {existing === null ? 'New film' : 'Edit film'}
                        </DialogTitle>
                        <DialogDescription>
                            Only the title is required. Everything else can be filled in
                            later, before the film is published.
                        </DialogDescription>
                    </DialogHeader>

                    <div className="space-y-2">
                        <Label htmlFor="movie-title">Title</Label>
                        <Input
                            id="movie-title"
                            value={title}
                            onChange={(event) => setTitle(event.target.value)}
                            required
                            maxLength={255}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="movie-original-title">Original title</Label>
                        <Input
                            id="movie-original-title"
                            value={originalTitle}
                            onChange={(event) => setOriginalTitle(event.target.value)}
                            placeholder="Only when it differs from the title"
                            maxLength={255}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="movie-overview">Overview</Label>
                        <Textarea
                            id="movie-overview"
                            value={overview}
                            onChange={(event) => setOverview(event.target.value)}
                            rows={3}
                        />
                    </div>

                    <div className="grid gap-3 sm:grid-cols-3">
                        <div className="space-y-2">
                            <Label htmlFor="movie-release">Release date</Label>
                            <Input
                                id="movie-release"
                                type="date"
                                value={releaseDate}
                                onChange={(event) => setReleaseDate(event.target.value)}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="movie-runtime">Runtime (min)</Label>
                            <Input
                                id="movie-runtime"
                                type="number"
                                min={1}
                                value={runtime}
                                onChange={(event) => setRuntime(event.target.value)}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="movie-language">Language</Label>
                            <Input
                                id="movie-language"
                                value={language}
                                onChange={(event) => setLanguage(event.target.value)}
                                placeholder="en"
                                maxLength={10}
                            />
                        </div>
                    </div>

                    <div className="grid gap-3 sm:grid-cols-2">
                        <div className="space-y-2">
                            <Label htmlFor="movie-poster">Poster path</Label>
                            <Input
                                id="movie-poster"
                                value={posterPath}
                                onChange={(event) => setPosterPath(event.target.value)}
                                placeholder="/abc123.jpg"
                                maxLength={1024}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="movie-backdrop">Backdrop path</Label>
                            <Input
                                id="movie-backdrop"
                                value={backdropPath}
                                onChange={(event) => setBackdropPath(event.target.value)}
                                placeholder="/xyz789.jpg"
                                maxLength={1024}
                            />
                        </div>
                    </div>

                    <div className="grid gap-3 sm:grid-cols-2">
                        <div className="space-y-2">
                            <Label htmlFor="movie-tmdb">TMDB id</Label>
                            <Input
                                id="movie-tmdb"
                                type="number"
                                min={1}
                                value={tmdbId}
                                onChange={(event) => setTmdbId(event.target.value)}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="movie-imdb">IMDb id</Label>
                            <Input
                                id="movie-imdb"
                                value={imdbId}
                                onChange={(event) => setImdbId(event.target.value)}
                                placeholder="tt0111161"
                                maxLength={20}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label>Genres</Label>
                        <div className="flex flex-wrap gap-2">
                            {genres?.map((genre) => (
                                <Button
                                    key={genre.uuid}
                                    type="button"
                                    size="sm"
                                    variant={
                                        genreUuids.includes(genre.uuid) ? 'default' : 'outline'
                                    }
                                    onClick={() => toggleGenre(genre.uuid)}
                                    aria-pressed={genreUuids.includes(genre.uuid)}
                                >
                                    {genre.name}
                                </Button>
                            ))}
                        </div>
                    </div>

                    <label className="flex items-center gap-2 text-sm">
                        <input
                            type="checkbox"
                            checked={published}
                            onChange={(event) => setPublished(event.target.checked)}
                            className="size-4"
                        />
                        Visible in the public catalogue
                    </label>

                    {save.isError && (
                        <p role="alert" className="text-sm text-destructive">
                            {errorMessage(save.error)}
                        </p>
                    )}

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="ghost"
                            onClick={() => onOpenChange(false)}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={save.isPending}>
                            {save.isPending ? 'Saving…' : 'Save'}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}