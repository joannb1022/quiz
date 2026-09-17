import { Link, useLocation } from 'react-router-dom'
import { BookOpen, RotateCcw, PlusCircle } from 'lucide-react'
import { cn } from '@/lib/cn'

export default function Layout({ children }: { children: React.ReactNode }) {
  const { pathname } = useLocation()

  const navItems = [
    { to: '/', label: 'Moje quizy', icon: BookOpen },
    { to: '/review', label: 'Do powtórzenia', icon: RotateCcw },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link to="/" className="font-semibold text-gray-900 text-lg">
            QuizAI
          </Link>
          <nav className="flex items-center gap-1">
            {navItems.map(({ to, label, icon: Icon }) => (
              <Link
                key={to}
                to={to}
                className={cn(
                  'flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors',
                  pathname === to
                    ? 'bg-gray-100 text-gray-900'
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                )}
              >
                <Icon size={15} />
                {label}
              </Link>
            ))}
            <Link
              to="/quiz/new"
              className="flex items-center gap-1.5 ml-2 px-3 py-1.5 rounded-md text-sm font-medium bg-violet-600 text-white hover:bg-violet-700 transition-colors"
            >
              <PlusCircle size={15} />
              Nowy quiz
            </Link>
          </nav>
        </div>
      </header>
      <main className="max-w-4xl mx-auto px-4 py-8">{children}</main>
    </div>
  )
}
