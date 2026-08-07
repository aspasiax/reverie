import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { LoginPage } from '@/pages/LoginPage'
import { MoviePage } from '@/pages/MoviePage'
import { MoviesPage } from '@/pages/MoviesPage'
import {WatchLogsPage} from "@/pages/WatchLogsPage.tsx";

/**
 * The route table of the application.
 *
 * Everything except the sign in screen sits behind the route guard, which
 * waits for the stored token to be verified before deciding whether to
 * render the page or redirect.
 */
function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<ProtectedRoute />}>
                <Route path="/" element={<MoviesPage />} />
                <Route path="/movies/:uuid" element={<MoviePage />} />
                <Route path="/watch-logs" element={<WatchLogsPage />} />
            </Route>

            {/* Unknown addresses fall back to the catalogue rather than a dead end. */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

export default App