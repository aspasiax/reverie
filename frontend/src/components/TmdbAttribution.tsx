/**
 * Credits TMDB as the source of the film images.
 *
 * The TMDB terms of use require applications built on their data to state
 * that the product is neither endorsed nor certified by them, so this notice
 * appears on every screen that displays a poster or a backdrop.
 */
export function TmdbAttribution() {
    return (
        <footer className="border-t px-6 py-4 text-center text-xs text-muted-foreground">
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
        </footer>
    )
}