import { useState } from 'react'
import { Moon, Sun } from 'lucide-react'
import { applyTheme, currentTheme } from '@/lib/theme'
import { Button } from '@/components/ui/button'

/**
 * Switches the interface between its two appearances.
 *
 * The state starts from what is already on screen rather than from a
 * default, because the appearance is settled before React runs: an inline
 * script in the page applies the remembered choice, so that a reader who
 * chose the light theme never sees the dark one flash first.
 */
export function ThemeToggle() {
    const [theme, setTheme] = useState(currentTheme)

    const next = theme === 'dark' ? 'light' : 'dark'

    function switchTheme() {
        applyTheme(next)
        setTheme(next)
    }

    return (
        <Button
            variant="ghost"
            size="sm"
            onClick={switchTheme}
            aria-label={`Switch to the ${next} theme`}
            title={`Switch to the ${next} theme`}
        >
            {theme === 'dark' ? (
                <Sun className="size-4" />
            ) : (
                <Moon className="size-4" />
            )}
        </Button>
    )
}