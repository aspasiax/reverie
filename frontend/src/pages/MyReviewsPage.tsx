import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { PenLine } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Page, Review } from '@/types/api'
import { buttonVariants } from '@/components/ui/button'
import { ReviewCard } from '@/components/ReviewCard'

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
        <div className="mx-auto max-w-3xl space-y-6 px-4 py-6 sm:px-6">
            <h1 className="text-2xl font-semibold">My reviews</h1>

            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {data !== undefined && data.totalElements === 0 && (
                <div className="rounded-lg border border-dashed p-10 text-center">
                    <PenLine className="mx-auto size-8 text-muted-foreground" />
                    <p className="mt-3 font-medium">Nothing written yet</p>
                    <p className="mt-1 text-sm text-muted-foreground">
                        A review belongs to a film you have watched. Log one as
                        watched and the page will ask you what you thought.
                    </p>
                    <Link
                        to="/watch-logs"
                        className={buttonVariants({
                            variant: 'outline',
                            size: 'sm',
                            className: 'mt-4',
                        })}
                    >
                        See what you have watched
                    </Link>
                </div>
            )}

            {data !== undefined && data.totalElements > 0 && (
                <>
                    <p className="text-sm text-muted-foreground">
                        {data.totalElements} reviews
                    </p>

                    <ul className="space-y-4">
                        {data.content.map((review) => (
                            <ReviewCard key={review.uuid} review={review} />
                        ))}
                    </ul>
                </>
            )}
        </div>
    )
}