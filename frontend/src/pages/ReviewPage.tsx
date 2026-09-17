import { useState } from 'react'
import { useReviewItems, useRemoveFromReview } from '@/api/review'
import type { ReviewQuestionDto } from '@/api/types'
import { cn } from '@/lib/cn'
import { RotateCcw, ChevronDown, ChevronUp, CheckCheck } from 'lucide-react'

export default function ReviewPage() {
  const { data: items, isLoading } = useReviewItems()
  const remove = useRemoveFromReview()

  if (isLoading) {
    return <div className="text-center text-gray-500 py-16">Ładowanie...</div>
  }

  if (!items?.length) {
    return (
      <div className="text-center py-24">
        <RotateCcw size={48} className="mx-auto text-gray-300 mb-4" />
        <h2 className="text-xl font-medium text-gray-700 mb-2">Brak pytań do powtórzenia</h2>
        <p className="text-gray-500">Pytania pojawią się tutaj po błędnych odpowiedziach lub ręcznym oznaczeniu.</p>
      </div>
    )
  }

  return (
    <div>
      <h1 className="text-2xl font-semibold text-gray-900 mb-1">Do powtórzenia</h1>
      <p className="text-sm text-gray-500 mb-6">{items.length} pytań</p>
      <div className="space-y-3">
        {items.map((item) => (
          <ReviewCard
            key={item.id}
            item={item}
            onMastered={() => remove.mutate(item.id)}
          />
        ))}
      </div>
    </div>
  )
}

function ReviewCard({ item, onMastered }: { item: ReviewQuestionDto; onMastered: () => void }) {
  const [expanded, setExpanded] = useState(false)

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div className="p-4">
        <div className="flex items-start justify-between gap-4">
          <div className="flex-1 min-w-0">
            <p className="text-xs text-gray-400 mb-1">{item.quizName}</p>
            <p className="text-gray-900 text-sm leading-relaxed">{item.content}</p>
          </div>
          <span className={cn(
            'shrink-0 text-xs font-medium px-2 py-0.5 rounded-full',
            item.type === 'CLOSED' ? 'bg-blue-50 text-blue-700' : 'bg-amber-50 text-amber-700'
          )}>
            {item.type === 'CLOSED' ? 'Zamknięte' : 'Otwarte'}
          </span>
        </div>

        <div className="flex items-center gap-2 mt-3">
          <button
            onClick={() => setExpanded((e) => !e)}
            className="flex items-center gap-1 text-xs text-gray-500 hover:text-gray-700 transition-colors"
          >
            {expanded ? <ChevronUp size={13} /> : <ChevronDown size={13} />}
            {expanded ? 'Ukryj odpowiedź' : 'Pokaż odpowiedź'}
          </button>
          <button
            onClick={onMastered}
            className="ml-auto flex items-center gap-1 text-xs text-green-600 hover:text-green-700 bg-green-50 hover:bg-green-100 px-2.5 py-1 rounded-lg transition-colors"
          >
            <CheckCheck size={13} />
            Opanowane
          </button>
        </div>
      </div>

      {expanded && (
        <div className="border-t border-gray-100 px-4 py-3 bg-gray-50">
          {item.type === 'CLOSED' && item.options && (
            <div className="mb-2 space-y-1">
              {Object.entries(item.options).map(([key, value]) => (
                <p
                  key={key}
                  className={cn(
                    'text-sm',
                    key === item.correctAnswer ? 'font-medium text-green-700' : 'text-gray-500'
                  )}
                >
                  {key}. {value} {key === item.correctAnswer && '✓'}
                </p>
              ))}
            </div>
          )}
          {item.type === 'OPEN' && (
            <p className="text-sm text-gray-700">
              <span className="font-medium text-gray-900">Wzorcowa odpowiedź: </span>
              {item.correctAnswer}
            </p>
          )}
          {item.explanation && (
            <p className="text-xs text-gray-500 mt-2">{item.explanation}</p>
          )}
        </div>
      )}
    </div>
  )
}
