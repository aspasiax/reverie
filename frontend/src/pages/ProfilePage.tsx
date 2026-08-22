import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { KeyRound, Pencil } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { joinedLabel } from '@/lib/dates'
import { myReviewsQuery } from '@/lib/reviews'
import { Button, buttonVariants } from '@/components/ui/button'
import { Avatar } from '@/components/Avatar'
import { FavouriteFilm } from '@/components/FavouriteFilm'
import { PasswordEditor } from '@/components/PasswordEditor'
import { ProfileEditor } from '@/components/ProfileEditor'
import { ReviewCard } from '@/components/ReviewCard'
import { UserStatistics } from '@/components/UserStatistics'

/** How many reviews the profile shows before handing over to their own screen. */
const recentReviews = 3

/**
 * The profile of the authenticated reader.
 *
 * Shown as everyone else sees it, with the private additions only the owner
 * is allowed: the email address, the role, and the two controls that change
 * them. Editing lives behind those controls rather than in the page, so that
 * opening your own profile answers the question other people are answering
 * when they open it — what you watch and what you thought — instead of
 * presenting a form.
 *
 * Only the newest few reviews appear. The rest have a screen of their own in
 * the navigation, and repeating all of them here would make that screen a
 * second copy rather than a destination.
 */
export function ProfilePage() {
    const { user } = useAuth()

    const [isEditingProfile, setIsEditingProfile] = useState(false)
    const [isChangingPassword, setIsChangingPassword] = useState(false)

    const { data: reviews } = useQuery(myReviewsQuery)

    if (user === null) {
        return null
    }

    return (
        <div className="mx-auto max-w-3xl space-y-6 px-4 py-6 sm:px-6">
            <div className="flex flex-wrap items-start justify-between gap-4">
                <div className="flex items-center gap-4">
                    <Avatar
                        name={user.displayName}
                        seed={user.username}
                        imageUrl={user.profileImageUrl}
                    />

                    <div className="min-w-0 space-y-1">
                        <h1 className="truncate text-2xl font-semibold tracking-tight">
                            {user.displayName}
                        </h1>

                        {/*
                          * The email address and the role appear here and on no
                          * other profile. They identify the account rather than
                          * describe the person, and only its owner may see them.
                          */}
                        <p className="truncate text-sm text-muted-foreground">
                            @{user.username} · {user.email}
                        </p>

                        <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
                            {user.role !== 'USER' && (
                                <span className="rounded-full border px-2 py-0.5">
                                    {user.role}
                                </span>
                            )}
                            <span>member since {joinedLabel(user.createdAt)}</span>
                        </div>
                    </div>
                </div>

                <div className="flex flex-wrap gap-2">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setIsEditingProfile(true)}
                    >
                        <Pencil className="size-4" />
                        Edit profile
                    </Button>

                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setIsChangingPassword(true)}
                    >
                        <KeyRound className="size-4" />
                        Change password
                    </Button>
                </div>
            </div>

            {user.bio !== null && (
                <p className="leading-relaxed">{user.bio}</p>
            )}

            {user.favouriteMovie !== null && (
                <FavouriteFilm film={user.favouriteMovie} />
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
                        {reviews.content.slice(0, recentReviews).map((review) => (
                            <ReviewCard key={review.uuid} review={review} />
                        ))}
                    </ul>

                    {reviews.totalElements > recentReviews && (
                        <Link
                            to="/my-reviews"
                            className={buttonVariants({
                                variant: 'outline',
                                size: 'sm',
                            })}
                        >
                            See all {reviews.totalElements} reviews
                        </Link>
                    )}
                </section>
            )}

            {/*
              * Remounted on every opening, so the inputs always start from what
              * is saved rather than from whatever was typed and abandoned.
              */}
            {isEditingProfile && (
                <ProfileEditor
                    open={isEditingProfile}
                    onOpenChange={setIsEditingProfile}
                />
            )}

            {isChangingPassword && (
                <PasswordEditor
                    open={isChangingPassword}
                    onOpenChange={setIsChangingPassword}
                />
            )}
        </div>
    )
}