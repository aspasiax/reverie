import { Link } from 'react-router-dom'
import { Star } from 'lucide-react'
import type { Review } from '@/types/api'
import { tmdbImage } from '@/lib/images'

/**
 * One review, shown as a row about the film it concerns.
 *
 * The author is not named here. Every screen that uses this already says
 * whose reviews these are, either by being someone's profile or by being
 * the reader's own list.
 *
 * Renders a list item, so it belongs inside a list.
 */
export function ReviewCard({ review }: { review: Review }) {
    return (
        <li className="flex gap-4 rounded-lg border p-4">
            <Link to={`/movies/${review.movieUuid}`} className="w-16 shrink-0">
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
    )
}