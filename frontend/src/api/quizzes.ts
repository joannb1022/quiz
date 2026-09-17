import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import apiClient from '@/lib/api-client'
import type { DeleteQuizCheckResponse, QuizDto, QuestionDto } from './types'

export function useQuizzes() {
  return useQuery<QuizDto[]>({
    queryKey: ['quizzes'],
    queryFn: () => apiClient.get<QuizDto[]>('/quizzes').then((r) => r.data),
  })
}

export function useQuestions(quizId: string) {
  return useQuery<QuestionDto[]>({
    queryKey: ['quizzes', quizId, 'questions'],
    queryFn: () =>
      apiClient.get<QuestionDto[]>(`/quizzes/${quizId}/questions`).then((r) => r.data),
    enabled: !!quizId,
  })
}

export function useCreateQuiz() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (formData: FormData) =>
      apiClient.post<QuizDto>('/quizzes', formData).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['quizzes'] }),
  })
}

export function useCheckDeleteQuiz() {
  return useMutation({
    mutationFn: (quizId: string) =>
      apiClient.delete<DeleteQuizCheckResponse>(`/quizzes/${quizId}/check`).then((r) => r.data),
  })
}

export function useDeleteQuiz() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (quizId: string) => apiClient.delete(`/quizzes/${quizId}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['quizzes'] }),
  })
}
