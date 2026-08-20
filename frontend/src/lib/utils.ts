import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Joins class names and settles the conflicts Tailwind cannot.
 *
 * Two Tailwind classes that set the same property both survive plain
 * concatenation, and which one wins is then decided by their order in the
 * stylesheet rather than by the caller. This keeps the last one written,
 * which is what lets a component accept a className that genuinely
 * overrides its own defaults.
 *
 * @param inputs class names, conditionals and arrays in any combination
 * @returns one class string with the conflicts resolved
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}