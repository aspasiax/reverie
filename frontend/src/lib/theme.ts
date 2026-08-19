/** The two appearances the interface can take. */
export type Theme = 'light' | 'dark'

/**
 * Where the choice is kept.
 *
 * Deliberately the same string the inline script in index.html reads. That
 * script runs before this module is ever loaded, so the name cannot be
 * imported from here and has to agree by hand.
 */
const storageKey = 'reverie-theme'

/**
 * Reports the appearance currently in effect.
 *
 * Read from the document rather than from storage, because the document is
 * what the inline script has already acted on. Storage only holds a choice
 * that was made; the class holds what is actually on screen.
 *
 * @returns the appearance the page is wearing
 */
export function currentTheme(): Theme {
    return document.documentElement.classList.contains('dark') ? 'dark' : 'light'
}

/**
 * Puts an appearance on and remembers it.
 *
 * @param theme the appearance to switch to
 */
export function applyTheme(theme: Theme): void {
    document.documentElement.classList.toggle('dark', theme === 'dark')
    localStorage.setItem(storageKey, theme)
}