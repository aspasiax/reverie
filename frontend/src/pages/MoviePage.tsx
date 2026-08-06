import { useMutation, useQueryClient, useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { ArrowLeft, Eye, Star } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page, Review } from '@/types/api'
import { Button } from '@/components/ui/button'
import { tmdbImage } from '@/lib/images'

async function fetchMovie(uuid: string) {
    const { data } = await api.get<Movie>(`/api/movies/${uuid}`)
    return data
}

async function fetchReviews(uuid: string) {
    const { data } = await api.get<Page<Review>>(`/api/reviews/movie/${uuid}`, {
        params: { page: 0, size: 20 },
    })
    return data
}

async function createWatchLog(movieUuid: string) {
    const { data } = await api.post('/api/watch-logs', { movieUuid })
    return data
}

export function MoviePage() {
    const { uuid } = useParams<{ uuid: string }>()

    const {
        data: movie,
        isPending,
        isError,
        error,
    } = useQuery({
        queryKey: ['movie', uuid],
        queryFn: () => fetchMovie(uuid!),
        enabled: uuid !== undefined,
    })

    const { data: reviews } = useQuery({
        queryKey: ['reviews', 'movie', uuid],
        queryFn: () => fetchReviews(uuid!),
        enabled: uuid !== undefined,
    })

    const queryClient = useQueryClient()

    const logWatch = useMutation({
        mutationFn: () => createWatchLog(uuid!),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['watch-logs'] })
        },
    })

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

            <main className="mx-auto max-w-3xl space-y-8 p-6">
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
                                <span
                                    key={genre.uuid}
                                    className="rounded-full border px-2 py-0.5 text-xs text-muted-foreground"
                                >
          {genre.name}
        </span>
                            ))}
                        </div>

                        {movie.overview !== null && (
                            <p className="pt-2 leading-relaxed">{movie.overview}</p>
                        )}

                        <div className="pt-2">
                            <Button onClick={() => logWatch.mutate()} disabled={logWatch.isPending}>
                                <Eye className="size-4" />
                                {logWatch.isPending ? 'Saving…' : 'Log as watched'}
                            </Button>

                            {logWatch.isSuccess && (
                                <p className="mt-2 text-sm text-green-500">
                                    Added to your watch history.
                                </p>
                            )}

                            {logWatch.isError && (
                                <p role="alert" className="mt-2 text-sm text-destructive">
                                    {errorMessage(logWatch.error)}
                                </p>
                            )}
                        </div>
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

                    {reviews !== undefined && reviews.content.length === 0 && (
                        <p className="text-sm text-muted-foreground">
                            No one has reviewed this film yet.
                        </p>
                    )}

                    <ul className="space-y-4">
                        {reviews?.content.map((review) => (
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
            </main>
        </div>
    )
}