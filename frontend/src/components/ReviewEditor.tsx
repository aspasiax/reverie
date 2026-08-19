import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Pencil, Star, Trash2 } from 'lucide-react'
import { api, errorMessage } from '@/lib/api'
import type { Review } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'

interface ReviewEditorProps {
    /** The film being reviewed. */
    movieUuid: string
    /** The review the user already wrote, or null when they have none. */
    existing: Review | null
}

/**
 * Lets the authenticated user write, change or remove their own review.
 *
 * The three operations invalidate both the reviews of this film and the
 * user's own review list, because a single change affects both screens.
 *
 * The server rejects a review from someone who has not logged the film as
 * watched. That message is shown as it arrives rather than being guessed
 * here, so the rule lives in one place only.
 */
export function ReviewEditor({ movieUuid, existing }: ReviewEditorProps) {
    const queryClient = useQueryClient()

    const [isEditing, setIsEditing] = useState(existing === null)
    const [rating, setRating] = useState<number | null>(existing?.rating ?? null)
    const [text, setText] = useState(existing?.reviewText ?? '')

    /** Refreshes every list that shows reviews. */
    function invalidateReviews() {
        queryClient.invalidateQueries({ queryKey: ['reviews'] })
    }

    const save = useMutation({
        mutationFn: async () => {
            const body = {
                rating,
                reviewText: text.trim() === '' ? null : text.trim(),
            }

            if (existing === null) {
                await api.post('/api/reviews', { movieUuid, ...body })
            } else {
                await api.put(`/api/reviews/${existing.uuid}`, body)
            }
        },
        onSuccess: () => {
            invalidateReviews()
            setIsEditing(false)
        },
    })

    const remove = useMutation({
        mutationFn: () => api.delete(`/api/reviews/${existing!.uuid}`),
        onSuccess: invalidateReviews,
    })

    function handleSubmit(event: FormEvent) {
        event.preventDefault()
        save.mutate()
    }

    // The user has a review and is not currently changing it.
    if (existing !== null && !isEditing) {
        return (
            <div className="rounded-lg border border-primary/40 bg-primary/5 p-4">
                <div className="flex items-center justify-between">
                    <span className="text-sm font-medium">Your review</span>
                    <div className="flex gap-1">
                        <Button variant="ghost" size="sm" onClick={() => setIsEditing(true)}>
                            <Pencil className="size-4" />
                        </Button>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => remove.mutate()}
                            disabled={remove.isPending}
                            aria-label="Delete your review"
                        >
                            <Trash2 className="size-4" />
                        </Button>
                    </div>
                </div>

                {existing.rating !== null && (
                    <p className="mt-1 inline-flex items-center gap-1 text-sm">
                        <Star className="size-4 fill-current text-yellow-600 dark:text-yellow-500" />
                        {existing.rating}
                    </p>
                )}

                {existing.reviewText !== null && (
                    <p className="mt-2 text-sm leading-relaxed">{existing.reviewText}</p>
                )}

                {remove.isError && (
                    <p role="alert" className="mt-2 text-sm text-destructive">
                        {errorMessage(remove.error)}
                    </p>
                )}
            </div>
        )
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-3 rounded-lg border p-4">
            <p className="text-sm font-medium">
                {existing === null ? 'Write a review' : 'Edit your review'}
            </p>

            <div className="flex flex-wrap items-center gap-1">
                {Array.from({ length: 10 }, (_, index) => index + 1).map((value) => (
                    <button
                        key={value}
                        type="button"
                        onClick={() => setRating(rating === value ? null : value)}
                        aria-label={`Rate ${value} out of 10`}
                        className="p-0.5"
                    >
                        <Star
                            className={
                                rating !== null && value <= rating
                                    ? 'size-5 fill-current text-yellow-600 dark:text-yellow-500'
                                    : 'size-5 text-muted-foreground'
                            }
                        />
                    </button>
                ))}
                {rating !== null && (
                    <span className="ml-2 text-sm text-muted-foreground">{rating}/10</span>
                )}
            </div>

            <Textarea
                value={text}
                onChange={(event) => setText(event.target.value)}
                placeholder="What did you think?"
                rows={4}
                maxLength={5000}
            />

            {save.isError && (
                <p role="alert" className="text-sm text-destructive">
                    {errorMessage(save.error)}
                </p>
            )}

            <div className="flex gap-2">
                <Button type="submit" disabled={save.isPending}>
                    {save.isPending ? 'Saving…' : 'Save review'}
                </Button>

                {existing !== null && (
                    <Button
                        type="button"
                        variant="ghost"
                        onClick={() => setIsEditing(false)}
                    >
                        Cancel
                    </Button>
                )}
            </div>
        </form>
    )
}