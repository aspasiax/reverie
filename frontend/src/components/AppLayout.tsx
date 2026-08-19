import { NavLink, Outlet } from 'react-router-dom'
import { Clapperboard, LogOut } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { TmdbAttribution } from '@/components/TmdbAttribution'
import { ADMIN_CAPABILITIES } from '@/lib/capabilities'

/** The destinations available to every signed in user. */
const navigation = [
    { to: '/', label: 'Films', end: true },
    { to: '/activity', label: 'Activity', end: false },
    { to: '/watchlist', label: 'Watchlist', end: false },
    { to: '/watch-logs', label: 'History', end: false },
    { to: '/my-reviews', label: 'Reviews', end: false },
]

/** The destination reserved for accounts that may administer the catalogue. */
const adminNavigation = { to: '/admin', label: 'Admin', end: false }

/**
 * The shell shared by every screen behind the sign in wall.
 *
 * Keeping the header, the navigation and the attribution here means a new
 * screen only has to render its own content, and none of them can drift
 * apart as the application grows.
 */
export function AppLayout() {
    const { user, logout, can } = useAuth()

    // The administration area is only offered to accounts that can use it.
    const items = ADMIN_CAPABILITIES.some(can)
        ? [...navigation, adminNavigation]
        : navigation

    return (
        <div className="flex min-h-screen flex-col">
            <header className="sticky top-0 z-10 border-b bg-background/95 backdrop-blur">
                <div className="mx-auto flex h-14 max-w-7xl items-center gap-3 px-4 sm:gap-6 sm:px-6">
                    <NavLink
                        to="/"
                        className="flex shrink-0 items-center gap-2 font-semibold"
                    >
                        <Clapperboard className="size-5 text-primary" />
                        <span className="hidden sm:inline">Reverie</span>
                    </NavLink>

                    {/*
                      * The destinations scroll sideways when they no longer fit,
                      * which keeps every one of them reachable on a narrow screen
                      * without introducing a second way to navigate.
                      */}
                    <nav className="flex min-w-0 flex-1 items-center gap-1 overflow-x-auto">
                        {items.map((item) => (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                end={item.end}
                                className={({ isActive }) =>
                                    cn(
                                        'shrink-0 rounded-md px-3 py-1.5 text-sm transition-colors',
                                        isActive
                                            ? 'bg-accent font-medium text-accent-foreground'
                                            : 'text-muted-foreground hover:text-foreground',
                                    )
                                }
                            >
                                {item.label}
                            </NavLink>
                        ))}
                    </nav>

                    <div className="flex shrink-0 items-center gap-1 sm:gap-3">
                        <NavLink
                            to="/profile"
                            className={({ isActive }) =>
                                cn(
                                    'rounded-md px-2 py-1 text-sm transition-colors',
                                    isActive
                                        ? 'bg-accent font-medium text-accent-foreground'
                                        : 'text-muted-foreground hover:text-foreground',
                                )
                            }
                        >
                            {/* The name shortens to an initial rather than vanishing. */}
                            <span className="hidden sm:inline">{user?.displayName}</span>
                            <span className="sm:hidden">
                                {user?.displayName?.charAt(0).toUpperCase()}
                            </span>
                        </NavLink>
                        <Button variant="ghost" size="sm" onClick={logout}>
                            <LogOut className="size-4" />
                            <span className="hidden sm:inline">Sign out</span>
                        </Button>
                    </div>
                </div>
            </header>

            <main className="flex-1">
                <Outlet />
            </main>

            <TmdbAttribution />
        </div>
    )
}