import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Star, Users } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import { relativeTime } from '@/lib/dates'
import { tmdbImage } from '@/lib/images'
import type { Page, Review } from '@/types/api'
import { buttonVariants } from '@/components/ui/button'
import { Avatar } from '@/components/Avatar'

/**
 * Retrieves the most recent reviews written by anyone.
 *
 * Deliberately one page and no more. This is a window on what is happening
 * rather than an archive of it, and a reader who wants everything a person
 * wrote can open that person instead.
 */
async function fetchRecentReviews() {
    const { data } = await api.get<Page<Review>>('/api/reviews', {
        params: { page: 0, size: 20 },
    })
    return data
}

/**
 * What everyone has been watching lately.
 *
 * The application is otherwise a private journal: you meet other readers
 * only by opening a film they happened to write about. This is the one
 * screen where they arrive on their own.
 */
export function ActivityPage() {
    const { data, isPending, isError, error } = useQuery({
        queryKey: ['reviews', 'recent'],
        queryFn: fetchRecentReviews,
    })

    return (
        <div className="mx-auto max-w-3xl space-y-6 px-4 py-6 sm:px-6">
            <div className="space-y-1">
                <h1 className="text-2xl font-semibold">Activity</h1>
                <p className="text-sm text-muted-foreground">
                    The most recent reviews from every reader.
                </p>
            </div>

            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {data !== undefined && data.totalElements === 0 && (
                <div className="rounded-lg border border-dashed p-10 text-center">
                    <Users className="mx-auto size-8 text-muted-foreground" />
                    <p className="mt-3 font-medium">Nothing has happened yet</p>
                    <p className="mt-1 text-sm text-muted-foreground">
                        When anyone reviews a film it shows up here. Be the first.
                    </p>
                    <Link
                        to="/"
                        className={buttonVariants({
                            variant: 'outline',
                            size: 'sm',
                            className: 'mt-4',
                        })}
                    >
                        Browse films
                    </Link>
                </div>
            )}

            {data !== undefined && data.totalElements > 0 && (
                <ul className="space-y-3">
                    {data.content.map((review) => {
                        const poster = tmdbImage(review.posterPath, 'w185')

                        return (
                            <li
                                key={review.uuid}
                                className="flex gap-4 rounded-lg border p-4 transition-colors hover:bg-accent/30"
                            >
                                <Link to={`/users/${review.userUuid}`} className="shrink-0">
                                    <Avatar
                                        name={review.displayName}
                                        seed={review.username}
                                        imageUrl={review.profileImageUrl}
                                        className="size-10 text-sm"
                                    />
                                </Link>

                                <div className="min-w-0 flex-1">
                                    <p className="text-sm">
                                        <Link
                                            to={`/users/${review.userUuid}`}
                                            className="font-medium hover:underline"
                                        >
                                            {review.displayName}
                                        </Link>{' '}
                                        <span className="text-muted-foreground">
                                            {review.reviewText === null
                                                ? 'rated'
                                                : 'reviewed'}
                                        </span>{' '}
                                        <Link
                                            to={`/movies/${review.movieUuid}`}
                                            className="font-medium hover:underline"
                                        >
                                            {review.movieTitle}
                                        </Link>

                                        {review.rating !== null && (
                                            <span className="ml-2 inline-flex items-center gap-1 align-middle">
                                                <Star className="size-3.5 fill-current text-yellow-500" />
                                                {review.rating}
                                            </span>
                                        )}
                                    </p>

                                    <p className="text-xs text-muted-foreground">
                                        {relativeTime(review.createdAt)}
                                    </p>

                                    {review.reviewText !== null && (
                                        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                                            {review.reviewText}
                                        </p>
                                    )}
                                </div>

                                <Link
                                    to={`/movies/${review.movieUuid}`}
                                    className="w-12 shrink-0"
                                >
                                    <div className="aspect-[2/3] overflow-hidden rounded bg-muted">
                                        {poster !== null && (
                                            <img
                                                src={poster}
                                                alt=""
                                                loading="lazy"
                                                className="size-full object-cover"
                                            />
                                        )}
                                    </div>
                                </Link>
                            </li>
                        )
                    })}
                </ul>
            )}
        </div>
    )
}