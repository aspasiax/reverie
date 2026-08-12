import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { Star } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page, Review } from '@/types/api'
import { tmdbImage } from '@/lib/images'
import { useAuth } from '@/auth/AuthContext'
import { ReviewEditor } from '@/components/ReviewEditor'
import { WatchActions } from '@/components/WatchActions'
import { WatchlistButton } from '@/components/WatchlistButton'

/**
 * Retrieves a single film.
 *
 * @param uuid the public identifier of the film
 */
async function fetchMovie(uuid: string) {
    const { data } = await api.get<Movie>(`/api/movies/${uuid}`)
    return data
}

/**
 * Retrieves the reviews written for a film, newest first.
 *
 * @param uuid the public identifier of the film
 */
async function fetchReviews(uuid: string) {
    const { data } = await api.get<Page<Review>>(`/api/reviews/movie/${uuid}`, {
        params: { page: 0, size: 20 },
    })
    return data
}

/**
 * A single film with its details, its reviews and a way to log a viewing.
 *
 * The film and its reviews are fetched separately so that a slow review
 * list never delays the details, and so that logging a viewing invalidates
 * only what it affects.
 */
export function MoviePage() {
    const { uuid } = useParams<{ uuid: string }>()
    const { user } = useAuth()

    const {
        data: movie,
        isPending,
        isError,
        error,
    } = useQuery({
        queryKey: ['movie', uuid],
        queryFn: () => fetchMovie(uuid!),
        /*
         * The route parameter is typed as possibly undefined, so the queries wait
         * until it is present rather than requesting an address with "undefined"
         * in it.
         */
        enabled: uuid !== undefined,
    })

    const { data: reviews } = useQuery({
        queryKey: ['reviews', 'movie', uuid],
        queryFn: () => fetchReviews(uuid!),
        enabled: uuid !== undefined,
    })

    /*
     * The user's own review is picked out of the film's review list rather
     * than fetched separately. The list is paginated, so this only holds while
     * a film has few enough reviews to fit on the first page.
     */
    const myReview =
        reviews?.content.find((review) => review.userUuid === user?.uuid) ?? null

    const otherReviews =
        reviews?.content.filter((review) => review.userUuid !== user?.uuid) ?? []



    if (isPending) {
        return <p className="p-6 text-muted-foreground">Loading…</p>
    }

    if (isError || movie === undefined) {
        return (
            <p role="alert" className="p-6 text-destructive">
                {errorMessage(error)}
            </p>
        )
    }

    return (
        <div className="mx-auto max-w-3xl space-y-8 px-4 py-6 sm:px-6">
                {/* Backdrop banner, fading into the page background. */}
                {tmdbImage(movie.backdropPath, 'w780') !== null && (
                    <div className="relative -mx-6 -mt-6 mb-6 h-48 overflow-hidden sm:h-64">
                        <img
                            src={tmdbImage(movie.backdropPath, 'w780')!}
                            alt=""
                            className="size-full object-cover"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-background via-background/40 to-transparent" />
                    </div>
                )}

                <section className="flex flex-col gap-6 sm:flex-row">
                    {/* Poster */}
                    <div className="w-40 shrink-0 sm:w-48">
                        <div className="aspect-[2/3] overflow-hidden rounded-lg border bg-muted">
                            {tmdbImage(movie.posterPath, 'w342') !== null ? (
                                <img
                                    src={tmdbImage(movie.posterPath, 'w342')!}
                                    alt=""
                                    className="size-full object-cover"
                                />
                            ) : (
                                <div className="flex size-full items-center justify-center p-3 text-center text-xs text-muted-foreground">
                                    {movie.title}
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Details */}
                    <div className="flex-1 space-y-3">
                        <h1 className="text-3xl font-semibold">{movie.title}</h1>

                        {movie.originalTitle !== null && movie.originalTitle !== movie.title && (
                            <p className="text-muted-foreground">{movie.originalTitle}</p>
                        )}

                        <p className="text-sm text-muted-foreground">
                            {movie.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                            {movie.runtime !== null && ` · ${movie.runtime} min`}
                            {movie.originalLanguage !== null &&
                                ` · ${movie.originalLanguage.toUpperCase()}`}
                        </p>

                        <div className="flex flex-wrap gap-1">
                            {movie.genres.map((genre) => (
                                <Link
                                    key={genre.uuid}
                                    to={`/?genre=${genre.uuid}`}
                                    className="inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-xs text-muted-foreground transition-colors hover:border-primary hover:text-foreground"
                                >
                                    <span
                                        aria-hidden
                                        className="size-1.5 rounded-full"
                                        style={{ backgroundColor: genre.color ?? 'transparent' }}
                                    />
                                    {genre.name}
                                </Link>
                            ))}
                        </div>

                        {movie.overview !== null && (
                            <p className="pt-2 leading-relaxed">{movie.overview}</p>
                        )}

                        <WatchlistButton movieUuid={uuid!} />
                        <WatchActions movieUuid={uuid!} />
                    </div>
                </section>

                <section className="space-y-4">
                    <h2 className="text-lg font-medium">
                        Reviews
                        {reviews !== undefined && (
                            <span className="ml-2 text-sm text-muted-foreground">
                                {reviews.totalElements}
                            </span>
                        )}
                    </h2>

                    {/* The user's own review, or the form to write one. */}
                    {uuid !== undefined && reviews !== undefined && (
                        <ReviewEditor
                            key={myReview?.uuid ?? 'new'}
                            movieUuid={uuid}
                            existing={myReview}
                        />
                    )}

                    {otherReviews.length === 0 && reviews !== undefined && (
                        <p className="text-sm text-muted-foreground">
                            No one else has reviewed this film yet.
                        </p>
                    )}

                    <ul className="space-y-4">
                        {otherReviews.map((review) => (
                            <li key={review.uuid} className="rounded-lg border p-4">
                                <div className="flex items-center justify-between">
                                    <span className="font-medium">{review.username}</span>
                                    {review.rating !== null && (
                                        <span className="inline-flex items-center gap-1 text-sm">
                                          <Star className="size-4 fill-current text-yellow-500" />
                                                                        {review.rating}
                                        </span>
                                    )}
                                </div>
                                {review.reviewText !== null && (
                                    <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                                        {review.reviewText}
                                    </p>
                                )}
                            </li>
                        ))}
                    </ul>
                </section>
            </div>
    )
}