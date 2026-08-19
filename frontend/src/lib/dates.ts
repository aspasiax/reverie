/** The units a gap in time is described with, largest first. */
const units: [Intl.RelativeTimeFormatUnit, number][] = [
    ['year', 365 * 24 * 60 * 60 * 1000],
    ['month', 30 * 24 * 60 * 60 * 1000],
    ['week', 7 * 24 * 60 * 60 * 1000],
    ['day', 24 * 60 * 60 * 1000],
    ['hour', 60 * 60 * 1000],
    ['minute', 60 * 1000],
]

/**
 * Says how long ago something happened, in the reader's own language.
 *
 * The largest unit that fits is used, because nobody reads a feed wanting
 * to know that something happened fifty one hours ago.
 *
 * @param instant when it happened, as the API returns it
 * @returns a phrase such as "3 days ago", or "now" for the last minute
 */
export function relativeTime(instant: string): string {
    const elapsed = Date.now() - new Date(instant).getTime()
    const format = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' })

    for (const [unit, size] of units) {
        if (elapsed >= size) {
            return format.format(-Math.floor(elapsed / size), unit)
        }
    }

    return format.format(0, 'second')
}

/**
 * Names a month the way it is read, from the {@code YYYY-MM} it is keyed by.
 *
 * @param month the month key
 * @returns the month and year in the reader's own format
 */
export function monthLabel(month: string): string {
    return new Date(`${month}-01T00:00:00`).toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'long',
    })
}

/**
 * Names a day within a month that has already been named.
 *
 * <p>The time is fixed at local midnight on purpose. A bare date parses as
 * UTC, which in a western time zone lands on the evening before and shows
 * the wrong weekday.</p>
 *
 * @param date the recorded date
 * @returns the weekday and the day of the month
 */
export function dayLabel(date: string): string {
    return new Date(`${date}T00:00:00`).toLocaleDateString(undefined, {
        weekday: 'long',
        day: 'numeric',
    })
}