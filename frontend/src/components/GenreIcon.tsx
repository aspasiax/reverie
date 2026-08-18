import { genreIcons } from '@/lib/genreIcons'
import { cn } from '@/lib/utils'

interface GenreIconProps {
    /** The icon name stored with the genre, if it has one. */
    name: string | null
    /** The colour stored with the genre, used for the icon and the fallback. */
    color: string | null
    className?: string
}

/**
 * The mark that stands for a genre.
 *
 * A genre may carry no icon, or one whose name is not among those the
 * interface can draw — an older record, or a name that has since changed
 * in the icon library. Both fall back to a plain dot in the genre colour,
 * which is enough to tell two genres apart and never leaves a gap.
 */
export function GenreIcon({ name, color, className }: GenreIconProps) {
    const Icon = name === null ? undefined : genreIcons[name]

    if (Icon === undefined) {
        return (
            <span
                aria-hidden
                className={cn('size-2 shrink-0 rounded-full', className)}
                style={{ backgroundColor: color ?? 'currentColor' }}
            />
        )
    }

    return (
        <Icon
            aria-hidden
            className={cn('size-3.5 shrink-0', className)}
            style={{ color: color ?? undefined }}
        />
    )
}