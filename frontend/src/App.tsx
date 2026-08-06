import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { LoginPage } from '@/pages/LoginPage'
import { MoviePage } from '@/pages/MoviePage'
import { MoviesPage } from '@/pages/MoviesPage'
import {WatchLogsPage} from "@/pages/WatchLogsPage.tsx";

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<ProtectedRoute />}>
                <Route path="/" element={<MoviesPage />} />
                <Route path="/movies/:uuid" element={<MoviePage />} />
                <Route path="/watch-logs" element={<WatchLogsPage />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

export default App