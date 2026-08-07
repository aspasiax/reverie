import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowLeft, Star } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import { tmdbImage } from '@/lib/images'
import type { Page, Review } from '@/types/api'
import { TmdbAttribution } from '@/components/TmdbAttribution'

/**
 * Retrieves the reviews written by the authenticated user, newest first.
 */
async function fetchMyReviews() {
    const { data } = await api.get<Page<Review>>('/api/reviews/me', {
        params: { page: 0, size: 50 },
    })
    return data
}

/**
 * The reviews written by the authenticated user.
 *
 * Each entry links back to the film, where the review can be changed or
 * removed. Editing is deliberately not offered here as well, so the rules
 * around a review live on one screen only.
 */
export function MyReviewsPage() {
    const { data, isPending, isError, error } = useQuery({
        queryKey: ['reviews', 'me'],
        queryFn: fetchMyReviews,
    })

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

            <main className="mx-auto max-w-3xl space-y-6 p-6">
                <h1 className="text-2xl font-semibold">My reviews</h1>

                {isPending && <p className="text-muted-foreground">Loading…</p>}

                {isError && (
                    <p role="alert" className="text-destructive">
                        {errorMessage(error)}
                    </p>
                )}

                {data !== undefined && data.content.length === 0 && (
                    <p className="text-muted-foreground">
                        You have not reviewed anything yet. Open a film you have watched
                        and write the first one.
                    </p>
                )}

                {data !== undefined && data.content.length > 0 && (
                    <>
                        <p className="text-sm text-muted-foreground">
                            {data.totalElements} reviews
                        </p>

                        <ul className="space-y-4">
                            {data.content.map((review) => (
                                <li key={review.uuid} className="flex gap-4 rounded-lg border p-4">
                                    <Link
                                        to={`/movies/${review.movieUuid}`}
                                        className="w-16 shrink-0"
                                    >
                                        <div className="aspect-[2/3] overflow-hidden rounded bg-muted">
                                            {tmdbImage(review.posterPath, 'w185') !== null && (
                                                <img
                                                    src={tmdbImage(review.posterPath, 'w185')!}
                                                    alt=""
                                                    loading="lazy"
                                                    className="size-full object-cover"
                                                />
                                            )}
                                        </div>
                                    </Link>

                                    <div className="min-w-0 flex-1">
                                        <div className="flex items-start justify-between gap-2">
                                            <Link
                                                to={`/movies/${review.movieUuid}`}
                                                className="font-medium hover:underline"
                                            >
                                                {review.movieTitle}
                                            </Link>

                                            {review.rating !== null && (
                                                <span className="inline-flex shrink-0 items-center gap-1 text-sm">
                          <Star className="size-4 fill-current text-yellow-500" />
                                                    {review.rating}
                        </span>
                                            )}
                                        </div>

                                        <p className="text-xs text-muted-foreground">
                                            {review.createdAt.slice(0, 10)}
                                        </p>

                                        {review.reviewText !== null && (
                                            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                                                {review.reviewText}
                                            </p>
                                        )}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </>
                )}
            </main>

            <TmdbAttribution />
        </div>
    )
}