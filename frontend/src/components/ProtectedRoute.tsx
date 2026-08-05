import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/auth/AuthContext'

/**
 * Guards the routes that require a signed in user.
 *
 * While the stored token is being verified the guard renders nothing but a
 * placeholder. Redirecting during that moment would send the user to the
 * login screen and immediately back again on every page reload.
 */
export function ProtectedRoute() {
    const { isAuthenticated, isLoading } = useAuth()

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center text-muted-foreground">
                Loading…
            </div>
        )
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />
    }

    return <Outlet />
}