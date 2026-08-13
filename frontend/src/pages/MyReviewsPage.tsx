import { useQuery } from '@tanstack/react-query'
import { api, errorMessage } from '@/lib/api'
import type { Page, Review } from '@/types/api'
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
                            <ReviewCard key={review.uuid} review={review} />
                        ))}
                    </ul>
                </>
            )}
        </div>
    )
}