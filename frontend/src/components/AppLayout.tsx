import { NavLink, Outlet } from 'react-router-dom'
import { Clapperboard, LogOut } from 'lucide-react'
import { useAuth } from '@/auth/AuthContext'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { TmdbAttribution } from '@/components/TmdbAttribution'

/** The destinations available to every signed in user. */
const navigation = [
    { to: '/', label: 'Films', end: true },
    { to: '/watch-logs', label: 'History', end: false },
    { to: '/my-reviews', label: 'Reviews', end: false },
]

/**
 * The shell shared by every screen behind the sign in wall.
 *
 * Keeping the header, the navigation and the attribution here means a new
 * screen only has to render its own content, and none of them can drift
 * apart as the application grows.
 */
export function AppLayout() {
    const { user, logout } = useAuth()

    return (
        <div className="flex min-h-screen flex-col">
            <header className="sticky top-0 z-10 border-b bg-background/95 backdrop-blur">
                <div className="mx-auto flex h-14 max-w-7xl items-center gap-6 px-4 sm:px-6">
                    <NavLink to="/" className="flex items-center gap-2 font-semibold">
                        <Clapperboard className="size-5 text-primary" />
                        Reverie
                    </NavLink>

                    <nav className="flex items-center gap-1">
                        {navigation.map((item) => (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                end={item.end}
                                className={({ isActive }) =>
                                    cn(
                                        'rounded-md px-3 py-1.5 text-sm transition-colors',
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

                    <div className="ml-auto flex items-center gap-3">
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
                            {user?.displayName}
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