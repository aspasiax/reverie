import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import axios from 'axios'
import { AuthProvider } from '@/auth/AuthContext'
import App from '@/App'
import './index.css'

/**
 * Creates the query client shared by the whole application.
 *
 * The defaults here apply to every query unless a screen overrides them.
 */
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            /*
             * Data is considered fresh for half a minute, which stops the
             * catalogue from being refetched every time a screen regains focus.
             */
            staleTime: 30_000,
            /*
             * Retrying a request the server has already rejected is pointless.
             * A 404 will stay a 404, and a 401 or 403 is answered by the
             * interceptor rather than by trying again.
             */
            retry: (failureCount, error) => {
                if (axios.isAxiosError(error)) {
                    const status = error.response?.status
                    if (status !== undefined && status >= 400 && status < 500) {
                        return false
                    }
                }
                return failureCount < 2
            },
        },
    },
})

/*
 * The providers are nested from the most general to the most specific:
 * data fetching, then routing, then the authenticated user, who is only
 * meaningful once both exist.
 */
createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <AuthProvider>
                    <App />
                </AuthProvider>
            </BrowserRouter>
        </QueryClientProvider>
    </StrictMode>,
)