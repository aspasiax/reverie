import {
    Compass,
    Drama,
    Film,
    FingerprintPattern,
    Flame,
    Ghost,
    Globe,
    Heart,
    Laugh,
    Moon,
    Mountain,
    Music,
    Popcorn,
    Rocket,
    Search,
    Skull,
    Sparkles,
    Swords,
    Wand,
    Zap,
} from 'lucide-react'
import type { LucideIcon } from 'lucide-react'

/**
 * The icons a genre may carry.
 *
 * The set is closed and listed here rather than loaded by name at runtime.
 * Naming an icon dynamically would mean shipping the whole library, or
 * fetching each one separately; twenty entries cost nothing and are the
 * only ones the administration form offers, so a genre can never hold a
 * name that has no picture behind it.
 */
export const genreIcons: Record<string, LucideIcon> = {
    swords: Swords,
    compass: Compass,
    sparkles: Sparkles,
    laugh: Laugh,
    'fingerprint-pattern': FingerprintPattern,
    drama: Drama,
    wand: Wand,
    ghost: Ghost,
    search: Search,
    heart: Heart,
    rocket: Rocket,
    zap: Zap,
    film: Film,
    music: Music,
    globe: Globe,
    mountain: Mountain,
    skull: Skull,
    flame: Flame,
    moon: Moon,
    popcorn: Popcorn,
}

/** The names above, in the order the administration form offers them. */
export const genreIconNames = Object.keys(genreIcons)