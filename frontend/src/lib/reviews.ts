import { queryOptions } from '@tanstack/react-query'
import { api } from '@/lib/api'
import type { Page, Review } from '@/types/api'

/** Retrieves the reviews written by the authenticated user, newest first. */
async function fetchMyReviews() {
    const { data } = await api.get<Page<Review>>('/api/reviews/me', {
        params: { page: 0, size: 50 },
    })
    return data
}

/**
 * The reviews written by the authenticated user.
 *
 * Read in two places: the profile shows them because that is what everyone
 * else sees there, and the reviews screen shows them because it is the one
 * the navigation points at. One definition means one request, answered from
 * the same cache whichever screen is opened first.
 */
export const myReviewsQuery = queryOptions({
    queryKey: ['reviews', 'me'],
    queryFn: fetchMyReviews,
})