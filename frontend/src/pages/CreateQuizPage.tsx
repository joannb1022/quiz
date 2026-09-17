import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCreateQuiz } from '@/api/quizzes'
import { Upload, FileText, Loader2 } from 'lucide-react'
import { cn } from '@/lib/cn'

export default function CreateQuizPage() {
  const navigate = useNavigate()
  const createQuiz = useCreateQuiz()
  const fileRef = useRef<HTMLInputElement>(null)

  const [name, setName] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [closedCount, setClosedCount] = useState(5)
  const [openCount, setOpenCount] = useState(3)
  const [dragOver, setDragOver] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!file || !name.trim()) return
    setError(null)

    const fd = new FormData()
    fd.append('name', name.trim())
    fd.append('file', file)
    fd.append('closedCount', String(closedCount))
    fd.append('openCount', String(openCount))

    try {
      const quiz = await createQuiz.mutateAsync(fd)
      navigate(`/quiz/${quiz.id}`)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message
      setError(msg ?? 'Wystąpił błąd. Spróbuj ponownie.')
    }
  }

  function handleFileDrop(e: React.DragEvent) {
    e.preventDefault()
    setDragOver(false)
    const dropped = e.dataTransfer.files[0]
    if (dropped?.type === 'application/pdf') setFile(dropped)
  }

  const isValid = file && name.trim() && closedCount + openCount > 0

  return (
    <div className="max-w-xl mx-auto">
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Nowy quiz</h1>

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Nazwa quizu</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="np. Psychopatologia — rozdział 3"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Notatki (PDF)</label>
          <div
            className={cn(
              'border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors',
              dragOver
                ? 'border-violet-400 bg-violet-50'
                : 'border-gray-200 hover:border-gray-300',
              file && 'border-green-400 bg-green-50'
            )}
            onDragOver={(e) => { e.preventDefault(); setDragOver(true) }}
            onDragLeave={() => setDragOver(false)}
            onDrop={handleFileDrop}
            onClick={() => fileRef.current?.click()}
          >
            {file ? (
              <div className="flex items-center justify-center gap-2 text-green-700">
                <FileText size={20} />
                <span className="text-sm font-medium">{file.name}</span>
              </div>
            ) : (
              <div className="text-gray-500">
                <Upload size={24} className="mx-auto mb-2 text-gray-400" />
                <p className="text-sm">Przeciągnij plik PDF lub kliknij, aby wybrać</p>
              </div>
            )}
          </div>
          <input
            ref={fileRef}
            type="file"
            accept="application/pdf"
            className="hidden"
            onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Pytania zamknięte
            </label>
            <input
              type="number"
              min={0}
              max={30}
              value={closedCount}
              onChange={(e) => setClosedCount(Number(e.target.value))}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Pytania otwarte
            </label>
            <input
              type="number"
              min={0}
              max={20}
              value={openCount}
              onChange={(e) => setOpenCount(Number(e.target.value))}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
            />
          </div>
        </div>

        {error && (
          <div className="text-red-600 text-sm bg-red-50 rounded-lg p-3">{error}</div>
        )}

        {createQuiz.isPending && (
          <div className="flex items-center gap-2 text-violet-600 text-sm bg-violet-50 rounded-lg p-3">
            <Loader2 size={16} className="animate-spin" />
            Generowanie pytań może zająć kilkanaście sekund…
          </div>
        )}

        <button
          type="submit"
          disabled={!isValid || createQuiz.isPending}
          className="w-full py-2.5 bg-violet-600 text-white rounded-lg font-medium text-sm hover:bg-violet-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {createQuiz.isPending ? 'Generowanie…' : 'Generuj quiz'}
        </button>
      </form>
    </div>
  )
}
