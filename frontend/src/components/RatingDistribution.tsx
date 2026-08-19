import { Star } from 'lucide-react'
import { cn } from '@/lib/utils'

interface RatingDistributionProps {
    /** The ratings that arrived with the reviews on this page. */
    ratings: number[]
    /** How many ratings the film has altogether, loaded here or not. */
    total: number
    /** The average the server calculated over all of them. */
    average: number
}

/**
 * How a film was rated, not just how well.
 *
 * An average of eight can mean everyone agreed, or that half the readers
 * loved it and half could not finish it. Those are different films to walk
 * into, and the number alone cannot tell them apart.
 *
 * The bars are drawn from the ratings the page happened to load. When that
 * is not all of them the shape is still true but the counts are not, and
 * the caption says so rather than letting the chart imply otherwise.
 */
export function RatingDistribution({ ratings, total, average }: RatingDistributionProps) {
    const counts = Array.from({ length: 10 }, (_, index) =>
        ratings.filter((rating) => rating === index + 1).length,
    )

    /*
     * The tallest bar defines full height. Scaling against the total instead
     * would flatten every film that most readers agreed about, which is the
     * one case the chart exists to show.
     */
    const highest = Math.max(...counts, 1)

    const isComplete = ratings.length === total

    return (
        <div className="flex items-end gap-5 rounded-lg border p-4">
            <div className="shrink-0">
                <p className="flex items-center gap-1.5 text-3xl font-semibold leading-none">
                    <Star className="size-5 fill-current text-yellow-500" />
                    {average.toFixed(1)}
                </p>

                <p className="mt-1.5 text-xs text-muted-foreground">
                    {isComplete
                        ? `${total} ${total === 1 ? 'rating' : 'ratings'}`
                        : `${ratings.length} of ${total} ratings`}
                </p>
            </div>

            <div className="min-w-0 flex-1">
                <div className="flex h-14 items-end gap-1">
                    {counts.map((count, index) => (
                        <div
                            key={index}
                            title={`${count} ${count === 1 ? 'rating' : 'ratings'} of ${index + 1}`}
                            className="flex h-full flex-1 items-end"
                        >
                            <div
                                className={cn(
                                    'w-full rounded-sm transition-colors',
                                    count === 0 ? 'bg-muted' : 'bg-primary/70',
                                )}
                                style={{
                                    height: count === 0 ? '2px' : `${(count / highest) * 100}%`,
                                }}
                            />
                        </div>
                    ))}
                </div>

                <div className="mt-1.5 flex justify-between text-xs text-muted-foreground">
                    <span>1</span>
                    <span>10</span>
                </div>
            </div>
        </div>
    )
}