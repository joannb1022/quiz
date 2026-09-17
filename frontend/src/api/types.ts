export type QuestionType = 'CLOSED' | 'OPEN'

export interface UserDto {
  id: string
  email: string
  createdAt: string
}

export interface QuizDto {
  id: string
  name: string
  createdAt: string
  questionCount: number
}

export interface QuestionDto {
  id: string
  content: string
  type: QuestionType
  options: Record<string, string> | null
}

export interface AnswerResponse {
  isCorrect: boolean
  correctAnswer: string
  explanation: string | null
}

export interface ReviewQuestionDto {
  id: string
  content: string
  type: QuestionType
  options: Record<string, string> | null
  correctAnswer: string
  explanation: string | null
  quizId: string
  quizName: string
}

export interface DeleteQuizCheckResponse {
  hasReviewItems: boolean
}
