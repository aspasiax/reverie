import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '@/auth/AuthContext'
import type { Capability } from '@/lib/capabilities'
import { cn } from '@/lib/utils'

/**
 * The sections of the administration area and the capability each needs.
 *
 * A section is only offered when the signed in user holds its capability,
 * so an account granted part of the administrative permissions sees exactly
 * the part it can use.
 */
const sections: { to: string; label: string; capability: Capability }[] = [
    { to: '/admin/overview', label: 'Overview', capability: 'STATISTICS_READ' },
    { to: '/admin/movies', label: 'Films', capability: 'MOVIE_CREATE' },
    { to: '/admin/genres', label: 'Genres', capability: 'GENRE_CREATE' },
    { to: '/admin/users', label: 'People', capability: 'USER_READ' },
]

/**
 * The shell shared by every administration screen.
 *
 * It renders the section navigation and leaves the content to the screen
 * itself, mirroring how the application layout works one level above.
 */
export function AdminLayout() {
    const { can } = useAuth()

    const visibleSections = sections.filter((section) => can(section.capability))

    return (
        <div className="mx-auto max-w-5xl space-y-6 px-4 py-6 sm:px-6">
            <div className="space-y-4">
                <div>
                    <h1 className="text-2xl font-semibold">Administration</h1>
                    <p className="text-sm text-muted-foreground">
                        Manage the catalogue and the accounts that use it.
                    </p>
                </div>

                <nav className="flex items-center gap-1 border-b">
                    {visibleSections.map((section) => (
                        <NavLink
                            key={section.to}
                            to={section.to}
                            className={({ isActive }) =>
                                cn(
                                    '-mb-px border-b-2 px-3 py-2 text-sm transition-colors',
                                    isActive
                                        ? 'border-primary font-medium text-foreground'
                                        : 'border-transparent text-muted-foreground hover:text-foreground',
                                )
                            }
                        >
                            {section.label}
                        </NavLink>
                    ))}
                </nav>
            </div>

            <Outlet />
        </div>
    )
}