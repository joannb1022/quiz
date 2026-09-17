import { Link } from 'react-router-dom'
import { useQuizzes, useCheckDeleteQuiz, useDeleteQuiz } from '@/api/quizzes'
import { BookOpen, PlusCircle, Trash2 } from 'lucide-react'
import { useState } from 'react'

export default function HomePage() {
  const { data: quizzes, isLoading } = useQuizzes()
  const checkDelete = useCheckDeleteQuiz()
  const deleteQuiz = useDeleteQuiz()
  const [confirmDelete, setConfirmDelete] = useState<{
    id: string
    name: string
    hasReviewItems: boolean
  } | null>(null)

  async function handleDeleteClick(id: string, name: string) {
    const result = await checkDelete.mutateAsync(id)
    setConfirmDelete({ id, name, hasReviewItems: result.hasReviewItems })
  }

  async function confirmDeleteQuiz() {
    if (!confirmDelete) return
    await deleteQuiz.mutateAsync(confirmDelete.id)
    setConfirmDelete(null)
  }

  if (isLoading) {
    return <div className="text-center text-gray-500 py-16">Ładowanie...</div>
  }

  if (!quizzes?.length) {
    return (
      <div className="text-center py-24">
        <BookOpen size={48} className="mx-auto text-gray-300 mb-4" />
        <h2 className="text-xl font-medium text-gray-700 mb-2">Brak quizów</h2>
        <p className="text-gray-500 mb-6">Wgraj notatki PDF i stwórz swój pierwszy quiz.</p>
        <Link
          to="/quiz/new"
          className="inline-flex items-center gap-2 px-4 py-2 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition-colors"
        >
          <PlusCircle size={16} />
          Stwórz quiz
        </Link>
      </div>
    )
  }

  return (
    <div>
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Moje quizy</h1>
      <div className="space-y-3">
        {quizzes.map((quiz) => (
          <div
            key={quiz.id}
            className="bg-white rounded-xl border border-gray-200 p-4 flex items-center justify-between hover:border-violet-200 transition-colors"
          >
            <Link to={`/quiz/${quiz.id}`} className="flex-1 min-w-0">
              <p className="font-medium text-gray-900 truncate">{quiz.name}</p>
              <p className="text-sm text-gray-500 mt-0.5">
                {quiz.questionCount} pytań ·{' '}
                {new Date(quiz.createdAt).toLocaleDateString('pl-PL')}
              </p>
            </Link>
            <button
              onClick={() => handleDeleteClick(quiz.id, quiz.name)}
              className="ml-4 p-2 text-gray-400 hover:text-red-500 transition-colors rounded-lg hover:bg-red-50"
              title="Usuń quiz"
            >
              <Trash2 size={16} />
            </button>
          </div>
        ))}
      </div>

      {confirmDelete && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-6 max-w-sm w-full shadow-xl">
            <h3 className="font-semibold text-gray-900 text-lg mb-2">Usuń quiz</h3>
            <p className="text-gray-600 text-sm mb-1">
              Czy na pewno chcesz usunąć <strong>{confirmDelete.name}</strong>?
            </p>
            {confirmDelete.hasReviewItems && (
              <p className="text-amber-600 text-sm bg-amber-50 rounded-lg p-3 mt-2">
                Ten quiz zawiera pytania w zakładce "Do powtórzenia". Zostaną one również usunięte.
              </p>
            )}
            <div className="flex gap-3 mt-5">
              <button
                onClick={() => setConfirmDelete(null)}
                className="flex-1 px-4 py-2 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
              >
                Anuluj
              </button>
              <button
                onClick={confirmDeleteQuiz}
                disabled={deleteQuiz.isPending}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                Usuń
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
