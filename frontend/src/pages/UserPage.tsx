import { useQuery } from '@tanstack/react-query'
import { useParams } from 'react-router-dom'
import { api, errorMessage } from '@/lib/api'
import type { Page, Review, UserSummary } from '@/types/api'
import { UserStatistics } from '@/components/UserStatistics'
import { ReviewCard } from '@/components/ReviewCard'

/** Retrieves the public profile of a user. */
async function fetchUser(uuid: string) {
    const { data } = await api.get<UserSummary>(`/api/users/${uuid}`)
    return data
}

/** Retrieves the reviews a user has written, newest first. */
async function fetchUserReviews(uuid: string) {
    const { data } = await api.get<Page<Review>>(`/api/reviews/user/${uuid}`, {
        params: { page: 0, size: 50 },
    })
    return data
}

/**
 * The public profile of another reader.
 *
 * It carries no email address and no role: those belong to the account
 * owner and to administration respectively. What is shown here is what
 * anyone could already piece together from the films themselves.
 */
export function UserPage() {
    const { uuid } = useParams<{ uuid: string }>()

    const { data: user, isPending, isError, error } = useQuery({
        queryKey: ['users', uuid],
        queryFn: () => fetchUser(uuid!),
        enabled: uuid !== undefined,
    })

    /*
     * The key sits under 'reviews', so writing or removing a review
     * refreshes this list along with every other place they appear.
     */
    const { data: reviews } = useQuery({
        queryKey: ['reviews', 'user', uuid],
        queryFn: () => fetchUserReviews(uuid!),
        enabled: uuid !== undefined,
    })

    return (
        <div className="mx-auto max-w-3xl space-y-6 px-4 py-6 sm:px-6">
            {isPending && <p className="text-muted-foreground">Loading…</p>}

            {isError && (
                <p role="alert" className="text-destructive">
                    {errorMessage(error)}
                </p>
            )}

            {user !== undefined && (
                <>
                    <div className="flex items-center gap-4">
                        <div className="flex size-16 shrink-0 items-center justify-center overflow-hidden rounded-full border bg-muted text-xl font-semibold">
                            {user.profileImageUrl !== null ? (
                                <img
                                    src={user.profileImageUrl}
                                    alt=""
                                    className="size-full object-cover"
                                />
                            ) : (
                                user.displayName.charAt(0).toUpperCase()
                            )}
                        </div>

                        <div className="min-w-0">
                            <h1 className="truncate text-2xl font-semibold">
                                {user.displayName}
                            </h1>
                            <p className="text-sm text-muted-foreground">
                                @{user.username} · joined{' '}
                                {new Date(user.createdAt).toLocaleDateString(undefined, {
                                    year: 'numeric',
                                    month: 'long',
                                })}
                            </p>
                        </div>
                    </div>

                    {user.bio !== null && (
                        <p className="leading-relaxed">{user.bio}</p>
                    )}

                    <UserStatistics uuid={user.uuid} />
                    {reviews !== undefined && reviews.totalElements > 0 && (
                        <section className="space-y-3">
                            <h2 className="text-lg font-medium">
                                Reviews
                                <span className="ml-2 text-sm text-muted-foreground">
                                    {reviews.totalElements}
                                </span>
                            </h2>

                            <ul className="space-y-4">
                                {reviews.content.map((review) => (
                                    <ReviewCard key={review.uuid} review={review} />
                                ))}
                            </ul>
                        </section>
                    )}
                </>
            )}
        </div>
    )
}