import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import apiClient from '@/lib/api-client'
import type { ReviewQuestionDto } from './types'

export function useReviewItems() {
  return useQuery<ReviewQuestionDto[]>({
    queryKey: ['review'],
    queryFn: () => apiClient.get<ReviewQuestionDto[]>('/review').then((r) => r.data),
  })
}

export function useRemoveFromReview() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (questionId: string) => apiClient.delete(`/review/${questionId}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['review'] }),
  })
}
