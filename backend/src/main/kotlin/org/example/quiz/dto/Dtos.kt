package org.example.quiz.dto

import org.example.quiz.entity.Question
import org.example.quiz.entity.QuestionType
import org.example.quiz.entity.Quiz
import org.example.quiz.entity.User
import java.time.Instant
import java.util.UUID

data class UserDto(
    val id: UUID,
    val email: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User) = UserDto(user.id, user.email, user.createdAt)
    }
}

data class QuizDto(
    val id: UUID,
    val name: String,
    val createdAt: Instant,
    val questionCount: Long,
) {
    companion object {
        fun from(quiz: Quiz, questionCount: Long) = QuizDto(quiz.id, quiz.name, quiz.createdAt, questionCount)
    }
}

data class QuestionDto(
    val id: UUID,
    val content: String,
    val type: QuestionType,
    val options: Map<String, String>?
) {
    companion object {
        fun from(q: Question) = QuestionDto(q.id, q.content, q.type, q.options)
    }
}

data class AnswerRequest(
    val answer: String,
)

data class AnswerResponse(
    val isCorrect: Boolean,
    val correctAnswer: String,
    val explanation: String?,
)

data class ReviewQuestionDto(
    val id: UUID,
    val content: String,
    val type: QuestionType,
    val options: Map<String, String>?,
    val correctAnswer: String,
    val explanation: String?,
    val quizId: UUID,
    val quizName: String,
) {
    companion object {
        fun from(q: Question) = ReviewQuestionDto(
            id = q.id,
            content = q.content,
            type = q.type,
            options = q.options,
            correctAnswer = q.correctAnswer,
            explanation = q.explanation,
            quizId = q.quiz.id,
            quizName = q.quiz.name,
        )
    }
}

data class DeleteQuizCheckResponse(
    val hasReviewItems: Boolean,
)
