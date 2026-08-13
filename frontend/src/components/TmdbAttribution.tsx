import { cn } from '@/lib/utils'

/**
 * The sentence TMDB asks to appear wherever their material is used.
 *
 * Kept separate from the footer so that screens outside the application
 * shell can carry it too. The words live in one place; only the placing
 * changes.
 */
export function TmdbNotice({ className }: { className?: string }) {
    return (
        <p className={cn('text-xs text-muted-foreground', className)}>
            This product uses the TMDB API but is not endorsed or certified by{' '}
            <a
                href="https://www.themoviedb.org"
                target="_blank"
                rel="noreferrer"
                className="underline hover:text-foreground"
            >
                TMDB
            </a>
            .
        </p>
    )
}

/**
 * The footer shown on every screen behind the sign in wall.
 */
export function TmdbAttribution() {
    return (
        <footer className="border-t px-6 py-4 text-center">
            <TmdbNotice />
        </footer>
    )
}