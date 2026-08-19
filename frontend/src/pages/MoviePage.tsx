import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { Star } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page, Review } from '@/types/api'
import { tmdbImage } from '@/lib/images'
import { useAuth } from '@/auth/AuthContext'
import { GenreIcon } from '@/components/GenreIcon'
import { RatingDistribution } from '@/components/RatingDistribution'
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
        <div className="mx-auto max-w-3xl space-y-10 px-4 pb-10 sm:px-6">
            {/*
              * The backdrop and the details form one header, so they live in a
              * wrapper of their own: the spacing between the page sections must
              * not come between a picture and the poster lifted onto it.
              */}
            <div>
                {tmdbImage(movie.backdropPath, 'w1280') !== null && (
                    <div className="relative -mx-4 h-56 overflow-hidden sm:-mx-6 sm:h-72">
                        <img
                            src={tmdbImage(movie.backdropPath, 'w1280')!}
                            alt=""
                            className="size-full object-cover"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-background via-background/60 to-background/10" />
                    </div>
                )}

                {/*
                  * The poster is lifted over the backdrop, and the details are
                  * pushed down to start where the picture ends, so the title
                  * never has to sit on top of it.
                  */}
                <section className="relative -mt-20 flex flex-col gap-6 sm:-mt-24 sm:flex-row">
                    <div className="w-36 shrink-0 sm:w-44">
                        <div className="aspect-[2/3] overflow-hidden rounded-lg bg-muted shadow-2xl ring-1 ring-black/10 dark:ring-white/10">
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

                    <div className="flex-1 space-y-3 sm:pt-24">
                        <div className="space-y-1">
                            <h1 className="text-3xl font-semibold tracking-tight">
                                {movie.title}
                            </h1>

                            {movie.originalTitle !== null &&
                                movie.originalTitle !== movie.title && (
                                    <p className="text-muted-foreground">
                                        {movie.originalTitle}
                                    </p>
                                )}
                        </div>

                        <p className="text-sm text-muted-foreground">
                            {movie.releaseDate?.slice(0, 4) ?? 'Unknown year'}
                            {movie.runtime !== null && ` · ${movie.runtime} min`}
                            {movie.originalLanguage !== null &&
                                ` · ${movie.originalLanguage.toUpperCase()}`}
                        </p>

                        <p className="flex flex-wrap items-center gap-x-3 gap-y-1 text-sm text-muted-foreground">
                            {movie.ratingCount > 0 && (
                                <span className="inline-flex items-center gap-1">
                                    <Star className="size-4 fill-current text-yellow-600 dark:text-yellow-500" />
                                    <span className="font-medium text-foreground">
                                        {movie.averageRating.toFixed(1)}
                                    </span>
                                    from {movie.ratingCount}{' '}
                                    {movie.ratingCount === 1 ? 'rating' : 'ratings'}
                                </span>
                            )}

                            {movie.watchCount > 0 && (
                                <span>
                                    watched {movie.watchCount}{' '}
                                    {movie.watchCount === 1 ? 'time' : 'times'}
                                </span>
                            )}
                        </p>

                        <div className="flex flex-wrap gap-1">
                            {movie.genres.map((genre) => (
                                <Link
                                    key={genre.uuid}
                                    to={`/?genre=${genre.uuid}`}
                                    className="inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-xs text-muted-foreground transition-colors hover:border-primary hover:text-foreground"
                                >
                                    <GenreIcon
                                        name={genre.icon}
                                        color={genre.color}
                                        className="size-3"
                                    />
                                    {genre.name}
                                </Link>
                            ))}
                        </div>

                        {movie.overview !== null && (
                            <p className="pt-1 leading-relaxed">{movie.overview}</p>
                        )}

                        {/* The two actions belong together, so they sit together. */}
                        <div className="flex flex-wrap items-start gap-2 pt-2">
                            <WatchlistButton movieUuid={uuid!} />
                            <WatchActions movieUuid={uuid!} />
                        </div>
                    </div>
                </section>
            </div>

            <section className="space-y-4">
                <h2 className="text-lg font-medium">
                    Reviews
                    {reviews !== undefined && (
                        <span className="ml-2 text-sm text-muted-foreground">
                            {reviews.totalElements}
                        </span>
                    )}
                </h2>
                
                {movie.ratingCount > 0 && reviews !== undefined && (
                    <RatingDistribution
                        ratings={reviews.content
                            .map((review) => review.rating)
                            .filter((rating) => rating !== null)}
                        total={movie.ratingCount}
                        average={movie.averageRating}
                    />
                )}

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
                        <li key={review.uuid} className="rounded-lg border bg-card/40 p-4">
                            <div className="flex items-center justify-between">
                                <Link
                                    to={`/users/${review.userUuid}`}
                                    className="font-medium hover:underline"
                                >
                                    {review.username}
                                </Link>

                                {review.rating !== null && (
                                    <span className="inline-flex items-center gap-1 text-sm">
                                        <Star className="size-4 fill-current text-yellow-600 dark:text-yellow-500" />
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