import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/auth/AuthContext'
import { ADMIN_CAPABILITIES } from '@/lib/capabilities'

/**
 * Guards the administration area.
 *
 * This guard is nested inside the authentication guard, so by the time it
 * runs the profile has already been loaded and only the permissions are
 * left to check. A user without any administrative capability is sent back
 * to the catalogue rather than shown an error, because the area was never
 * offered to them in the first place.
 */
export function AdminRoute() {
    const { can } = useAuth()

    if (!ADMIN_CAPABILITIES.some(can)) {
        return <Navigate to="/" replace />
    }

    return <Outlet />
}