import { useQuery } from '@tanstack/react-query'
import { Check } from 'lucide-react'
import { tmdbImage } from '@/lib/images'
import { watchLogsQuery } from '@/lib/watchLogs'
import { cn } from '@/lib/utils'

interface FavouriteFilmPickerProps {
    /** The film currently chosen, if any. */
    value: string | null
    /** Reports the film chosen, or null when the choice is cleared. */
    onChange: (movieUuid: string | null) => void
}

/**
 * Chooses a favourite film from those already watched.
 *
 * Posters rather than a list of titles: this is an application about films,
 * and a shelf of covers is how anyone actually recognises the one they mean.
 * Only watched films are offered, which is the same rule the API enforces —
 * a choice that cannot be saved should not be offered in the first place.
 */
export function FavouriteFilmPicker({ value, onChange }: FavouriteFilmPickerProps) {
    const { data, isPending } = useQuery(watchLogsQuery)

    /*
     * A film watched twice is still one film. The history is keyed by film so
     * that a rewatch does not put the same poster on the shelf again, and
     * sorted by title because a shelf is searched by eye.
     */
    const films = Array.from(
        new Map((data?.content ?? []).map((log) => [log.movieUuid, log])).values(),
    ).sort((first, second) => first.movieTitle.localeCompare(second.movieTitle))

    if (isPending) {
        return <p className="text-sm text-muted-foreground">Loading your history…</p>
    }

    if (films.length === 0) {
        return (
            <p className="text-sm text-muted-foreground">
                Log a film as watched and it will appear here.
            </p>
        )
    }

    return (
        <div className="grid max-h-64 grid-cols-4 gap-2 overflow-y-auto rounded-md border p-2 sm:grid-cols-6">
            {films.map((film) => {
                const isSelected = value === film.movieUuid
                const poster = tmdbImage(film.posterPath, 'w185')

                return (
                    <button
                        key={film.movieUuid}
                        type="button"
                        onClick={() => onChange(isSelected ? null : film.movieUuid)}
                        title={film.movieTitle}
                        aria-label={film.movieTitle}
                        aria-pressed={isSelected}
                        className={cn(
                            'relative aspect-2/3 overflow-hidden rounded-sm ring-offset-2 ring-offset-background transition-opacity',
                            isSelected
                                ? 'ring-2 ring-primary'
                                : 'opacity-70 hover:opacity-100',
                        )}
                    >
                        {poster === null ? (
                            <span className="flex size-full items-center justify-center bg-muted p-1 text-center text-[10px] leading-tight">
                                {film.movieTitle}
                            </span>
                        ) : (
                            <img
                                src={poster}
                                alt=""
                                loading="lazy"
                                className="size-full object-cover"
                            />
                        )}

                        {isSelected && (
                            <span className="absolute inset-0 flex items-center justify-center bg-background/60">
                                <Check className="size-5 text-primary" />
                            </span>
                        )}
                    </button>
                )
            })}
        </div>
    )
}