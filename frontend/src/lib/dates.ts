/**
 * The language every date is written in.
 *
 * Fixed rather than taken from the browser. The interface has exactly one
 * language and no translations, so a date that followed the reader's own
 * settings would be the single localised thing on an otherwise English
 * screen — a month heading in Greek above rows of English.
 */
const locale = 'en'

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
 * Says how long ago something happened.
 *
 * The largest unit that fits is used, because nobody reads a feed wanting
 * to know that something happened fifty one hours ago.
 *
 * @param instant when it happened, as the API returns it
 * @returns a phrase such as "3 days ago", or "now" for the last minute
 */
export function relativeTime(instant: string): string {
    const elapsed = Date.now() - new Date(instant).getTime()
    const format = new Intl.RelativeTimeFormat(locale, { numeric: 'auto' })

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
 * @returns the month and the year, as in "March 2026"
 */
export function monthLabel(month: string): string {
    return new Date(`${month}-01T00:00:00`).toLocaleDateString(locale, {
        year: 'numeric',
        month: 'long',
    })
}

/**
 * Names a day within a month that has already been named.
 *
 * The time is fixed at local midnight on purpose. A bare date parses as
 * UTC, which in a western time zone lands on the evening before and shows
 * the wrong weekday.
 *
 * @param date the recorded date
 * @returns the weekday and the day of the month, as in "Friday 15"
 */
export function dayLabel(date: string): string {
    return new Date(`${date}T00:00:00`).toLocaleDateString(locale, {
        weekday: 'long',
        day: 'numeric',
    })
}

/**
 * Names the month an account was created in.
 *
 * @param instant when the account was created, as the API returns it
 * @returns the month and the year, as in "March 2026"
 */
export function joinedLabel(instant: string): string {
    return new Date(instant).toLocaleDateString(locale, {
        year: 'numeric',
        month: 'long',
    })
}