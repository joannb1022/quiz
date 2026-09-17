import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import HomePage from '@/pages/HomePage'
import CreateQuizPage from '@/pages/CreateQuizPage'
import QuizSessionPage from '@/pages/QuizSessionPage'
import ReviewPage from '@/pages/ReviewPage'
import Layout from '@/components/Layout'

const queryClient = new QueryClient()

function AppRoutes() {
    return (
        <Layout>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/quiz/new" element={<CreateQuizPage />} />
                <Route path="/quiz/:id" element={<QuizSessionPage />} />
                <Route path="/review" element={<ReviewPage />} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </Layout>
    )
}

export default function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <AppRoutes />
            </BrowserRouter>
        </QueryClientProvider>
    )
}