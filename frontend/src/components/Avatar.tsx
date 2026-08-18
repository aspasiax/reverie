import { useState } from 'react'
import { cn } from '@/lib/utils'

/**
 * The colours an account can be given.
 *
 * Chosen to sit on a dark background and to carry white text, so the
 * initial stays legible whichever one an account lands on.
 */
const palette = [
    '#E11D48',
    '#F97316',
    '#F59E0B',
    '#10B981',
    '#0EA5E9',
    '#6366F1',
    '#A855F7',
    '#EC4899',
]

/**
 * Picks a colour for an account, always the same one for the same handle.
 *
 * Derived rather than stored: nobody has to choose, nothing has to be
 * saved, and an account looks like itself from the moment it is created.
 *
 * @param seed the handle to derive the colour from
 * @returns one of the palette colours
 */
function colourFor(seed: string): string {
    let total = 0

    for (const character of seed) {
        total += character.codePointAt(0) ?? 0
    }

    return palette[total % palette.length]
}

interface AvatarProps {
    /** The name the initial is taken from. */
    name: string
    /**
     * The handle the colour is derived from.
     *
     * Deliberately not the display name: changing what you are called
     * should not change the colour people recognise you by.
     */
    seed: string
    /** An image to show instead of the initial, if the account has one. */
    imageUrl: string | null
    className?: string
}

/**
 * The mark that stands for an account.
 *
 * An image when there is one, an initial on a colour of its own when there
 * is not. An address that fails to load falls back to the initial too, so
 * a mistyped link leaves a profile looking deliberate rather than broken.
 */
export function Avatar({ name, seed, imageUrl, className }: AvatarProps) {
    const [failed, setFailed] = useState(false)

    const showImage = imageUrl !== null && imageUrl !== '' && !failed

    return (
        <div
            className={cn(
                'flex size-16 shrink-0 items-center justify-center overflow-hidden rounded-full text-xl font-semibold text-white',
                className,
            )}
            style={showImage ? undefined : { backgroundColor: colourFor(seed) }}
        >
            {showImage ? (
                <img
                    src={imageUrl}
                    alt=""
                    className="size-full object-cover"
                    onError={() => setFailed(true)}
                />
            ) : (
                name.charAt(0).toUpperCase()
            )}
        </div>
    )
}