import { Link } from 'react-router-dom'
import { Heart } from 'lucide-react'
import { tmdbImage } from '@/lib/images'
import type { MovieSummary } from '@/types/api'

interface FavouriteFilmProps {
    /** The film the profile owner named as their favourite. */
    film: MovieSummary
}

/**
 * The film a reader names as their favourite, shown on their profile.
 *
 * Given its own poster rather than a line of text: it is the one film the
 * profile is making a claim about, and a claim about a film deserves to
 * look like the film.
 */
export function FavouriteFilm({ film }: FavouriteFilmProps) {
    const poster = tmdbImage(film.posterPath, 'w185')

    return (
        <section className="space-y-3">
            <h2 className="flex items-center gap-2 text-lg font-medium">
                <Heart className="size-4 text-primary" aria-hidden />
                Favourite film
            </h2>

            <Link
                to={`/movies/${film.uuid}`}
                className="flex items-center gap-4 rounded-lg border p-4 transition-colors hover:bg-accent/50"
            >
                <div className="w-16 shrink-0 overflow-hidden rounded-sm">
                    {poster === null ? (
                        <span className="flex aspect-2/3 items-center justify-center bg-muted text-xs text-muted-foreground">
                            —
                        </span>
                    ) : (
                        <img src={poster} alt="" className="w-full" />
                    )}
                </div>

                <div className="min-w-0">
                    <p className="truncate font-medium">{film.title}</p>

                    {film.releaseDate !== null && (
                        <p className="text-sm text-muted-foreground">
                            {film.releaseDate.slice(0, 4)}
                        </p>
                    )}
                </div>
            </Link>
        </section>
    )
}