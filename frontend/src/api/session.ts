import { useMutation, useQueryClient } from '@tanstack/react-query'
import apiClient from '@/lib/api-client'
import type { AnswerResponse } from './types'

export function useSubmitAnswer() {
  return useMutation({
    mutationFn: ({ questionId, answer }: { questionId: string; answer: string }) =>
      apiClient
        .post<AnswerResponse>(`/questions/${questionId}/answer`, { answer })
        .then((r) => r.data),
  })
}

export function useAddToReview() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (questionId: string) =>
      apiClient.post(`/questions/${questionId}/review`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['review'] }),
  })
}
