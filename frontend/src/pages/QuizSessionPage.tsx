import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { useQuestions } from '@/api/quizzes'
import { useSubmitAnswer, useAddToReview } from '@/api/session'
import type { QuestionDto, AnswerResponse } from '@/api/types'
import { cn } from '@/lib/cn'
import { CheckCircle2, XCircle, BookmarkPlus, Loader2, ChevronLeft, ChevronRight } from 'lucide-react'

interface QuestionState {
  answer: string
  result: AnswerResponse | null
  isLoading: boolean
  addedToReview: boolean
}

export default function QuizSessionPage() {
  const { id } = useParams<{ id: string }>()
  const { data: questions, isLoading } = useQuestions(id!)
  const submitAnswer = useSubmitAnswer()
  const addToReview = useAddToReview()

  const [current, setCurrent] = useState(0)
  const [states, setStates] = useState<Record<string, QuestionState>>({})

  if (isLoading) {
    return <div className="text-center text-gray-500 py-16">Ładowanie pytań...</div>
  }

  if (!questions?.length) {
    return <div className="text-center text-gray-500 py-16">Brak pytań w tym quizie.</div>
  }

  const question = questions[current]
  const state = states[question.id] ?? { answer: '', result: null, isLoading: false, addedToReview: false }

  function setQuestionState(id: string, patch: Partial<QuestionState>) {
    setStates((prev) => ({ ...prev, [id]: { ...prev[id] ?? { answer: '', result: null, isLoading: false, addedToReview: false }, ...patch } }))
  }

  async function handleAnswer() {
    if (!state.answer.trim() || state.result) return
    setQuestionState(question.id, { isLoading: true })
    try {
      const result = await submitAnswer.mutateAsync({ questionId: question.id, answer: state.answer })
      setQuestionState(question.id, { result, isLoading: false })
    } catch {
      setQuestionState(question.id, { isLoading: false })
    }
  }

  async function handleAddToReview() {
    await addToReview.mutateAsync(question.id)
    setQuestionState(question.id, { addedToReview: true })
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <span className="text-sm text-gray-500">
          Pytanie {current + 1} / {questions.length}
        </span>
        <div className="flex gap-1">
          {questions.map((q, i) => (
            <button
              key={q.id}
              onClick={() => setCurrent(i)}
              className={cn(
                'w-6 h-6 rounded text-xs font-medium transition-colors',
                i === current ? 'bg-violet-600 text-white' :
                states[q.id]?.result
                  ? states[q.id].result!.isCorrect
                    ? 'bg-green-100 text-green-700'
                    : 'bg-red-100 text-red-700'
                  : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
              )}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-200 p-6">
        <div className="flex items-start justify-between gap-4 mb-4">
          <p className="text-gray-900 font-medium leading-relaxed">{question.content}</p>
          <span className={cn(
            'shrink-0 text-xs font-medium px-2 py-0.5 rounded-full',
            question.type === 'CLOSED' ? 'bg-blue-50 text-blue-700' : 'bg-amber-50 text-amber-700'
          )}>
            {question.type === 'CLOSED' ? 'Zamknięte' : 'Otwarte'}
          </span>
        </div>

        {question.type === 'CLOSED' && question.options ? (
          <ClosedQuestion
            question={question}
            state={state}
            onSelect={(ans) => !state.result && setQuestionState(question.id, { answer: ans })}
          />
        ) : (
          <textarea
            value={state.answer}
            onChange={(e) => !state.result && setQuestionState(question.id, { answer: e.target.value })}
            placeholder="Wpisz swoją odpowiedź..."
            rows={4}
            disabled={!!state.result}
            className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-violet-500 disabled:bg-gray-50"
          />
        )}

        {state.result && <FeedbackPanel result={state.result} />}

        <div className="flex items-center justify-between mt-4">
          <button
            onClick={handleAddToReview}
            disabled={state.addedToReview || addToReview.isPending}
            className={cn(
              'flex items-center gap-1.5 text-sm px-3 py-1.5 rounded-lg transition-colors',
              state.addedToReview
                ? 'text-violet-600 bg-violet-50 cursor-default'
                : 'text-gray-500 hover:text-violet-600 hover:bg-violet-50'
            )}
          >
            <BookmarkPlus size={15} />
            {state.addedToReview ? 'Dodano do powtórzeń' : 'Dodaj do powtórzeń'}
          </button>

          {!state.result && (
            <button
              onClick={handleAnswer}
              disabled={!state.answer.trim() || state.isLoading}
              className="flex items-center gap-1.5 px-4 py-2 bg-violet-600 text-white rounded-lg text-sm font-medium hover:bg-violet-700 transition-colors disabled:opacity-50"
            >
              {state.isLoading && <Loader2 size={14} className="animate-spin" />}
              Sprawdź
            </button>
          )}
        </div>
      </div>

      <div className="flex justify-between mt-4">
        <button
          onClick={() => setCurrent((c) => Math.max(0, c - 1))}
          disabled={current === 0}
          className="flex items-center gap-1 px-3 py-2 text-sm text-gray-600 hover:text-gray-900 disabled:opacity-30 transition-colors"
        >
          <ChevronLeft size={16} />
          Poprzednie
        </button>
        <button
          onClick={() => setCurrent((c) => Math.min(questions.length - 1, c + 1))}
          disabled={current === questions.length - 1}
          className="flex items-center gap-1 px-3 py-2 text-sm text-gray-600 hover:text-gray-900 disabled:opacity-30 transition-colors"
        >
          Następne
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  )
}

function ClosedQuestion({
  question,
  state,
  onSelect,
}: {
  question: QuestionDto
  state: QuestionState
  onSelect: (ans: string) => void
}) {
  const options = question.options as Record<string, string>
  return (
    <div className="space-y-2">
      {Object.entries(options).map(([key, value]) => {
        const isSelected = state.answer === key
        const isCorrect = state.result?.correctAnswer === key
        const isWrong = state.result && isSelected && !isCorrect

        return (
          <button
            key={key}
            onClick={() => onSelect(key)}
            className={cn(
              'w-full text-left px-4 py-3 rounded-xl border text-sm transition-colors',
              !state.result && isSelected && 'border-violet-400 bg-violet-50',
              !state.result && !isSelected && 'border-gray-200 hover:border-gray-300',
              state.result && isCorrect && 'border-green-400 bg-green-50 text-green-800',
              state.result && isWrong && 'border-red-400 bg-red-50 text-red-800',
              state.result && !isCorrect && !isSelected && 'border-gray-200 text-gray-400',
            )}
          >
            <span className="font-medium mr-2">{key}.</span>
            {value}
          </button>
        )
      })}
    </div>
  )
}

function FeedbackPanel({ result }: { result: AnswerResponse }) {
  return (
    <div className={cn(
      'mt-4 rounded-xl p-4 flex gap-3',
      result.isCorrect ? 'bg-green-50' : 'bg-red-50'
    )}>
      {result.isCorrect
        ? <CheckCircle2 size={18} className="text-green-600 shrink-0 mt-0.5" />
        : <XCircle size={18} className="text-red-600 shrink-0 mt-0.5" />}
      <div>
        <p className={cn('text-sm font-medium', result.isCorrect ? 'text-green-800' : 'text-red-800')}>
          {result.isCorrect ? 'Poprawna odpowiedź!' : 'Niepoprawna odpowiedź'}
        </p>
        {!result.isCorrect && (
          <p className="text-sm text-red-700 mt-0.5">
            Poprawna: <strong>{result.correctAnswer}</strong>
          </p>
        )}
        {result.explanation && (
          <p className="text-sm text-gray-600 mt-1">{result.explanation}</p>
        )}
      </div>
    </div>
  )
}
