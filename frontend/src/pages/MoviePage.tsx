import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { ArrowLeft, Star } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Movie, Page, Review } from '@/types/api'

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
                <section className="space-y-3">
                    <h1 className="text-3xl font-semibold">{movie.title}</h1>

                    {movie.originalTitle !== null &&
                        movie.originalTitle !== movie.title && (
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