const TMDB_IMAGE_BASE = 'https://image.tmdb.org/t/p'

/**
 * Builds the address of a TMDB image from the relative path stored with a
 * film.
 *
 * The width is chosen by the caller because TMDB serves each image in
 * several sizes, and requesting a poster at backdrop width wastes
 * bandwidth for no visible gain. The smallest size is meant for the
 * thumbnails in the administration lists.
 *
 * @param path  the relative path stored in the database, if any
 * @param width the TMDB size token, for example w500
 * @returns the full address, or null when the film has no image
 */
export function tmdbImage(
    path: string | null,
    width: 'w92' | 'w185' | 'w342' | 'w500' | 'w780' | 'w1280' | 'original' = 'w342',
): string | null {
    if (path === null) {
        return null
    }

    return `${TMDB_IMAGE_BASE}/${width}${path}`
}