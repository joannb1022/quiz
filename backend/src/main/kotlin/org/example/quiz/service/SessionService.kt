package org.example.quiz.service

import org.example.quiz.dto.AnswerResponse
import org.example.quiz.entity.QuestionType
import org.example.quiz.entity.ReviewItem
import org.example.quiz.entity.ReviewItemId
import org.example.quiz.entity.UserAnswer
import org.example.quiz.entity.User
import org.example.quiz.repository.QuestionRepository
import org.example.quiz.repository.ReviewItemRepository
import org.example.quiz.repository.UserAnswerRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SessionService(
    private val questionRepository: QuestionRepository,
    private val userAnswerRepository: UserAnswerRepository,
    private val reviewItemRepository: ReviewItemRepository,
    private val geminiService: GeminiService,
    private val reviewService: ReviewService,
) {

    @Transactional
    fun submitAnswer(user: User, questionId: UUID, userAnswer: String): AnswerResponse {
        val question = questionRepository.findById(questionId)
            .orElseThrow { NoSuchElementException("Pytanie nie istnieje") }

        val (isCorrect, explanation) = when (question.type) {
            QuestionType.CLOSED -> {
                val correct = userAnswer.trim().uppercase() == question.correctAnswer.trim().uppercase()
                correct to question.explanation
            }
            QuestionType.OPEN -> {
                val eval = geminiService.evaluateOpenAnswer(question.content, question.correctAnswer, userAnswer)
                eval.isCorrect to eval.explanation
            }
        }

        userAnswerRepository.save(
            UserAnswer(question = question, user = user, userAnswer = userAnswer, isCorrect = isCorrect)
        )

        if (!isCorrect) {
            val reviewId = ReviewItemId(questionId = question.id, userId = user.id)
            if (!reviewItemRepository.existsById(reviewId)) {
                reviewItemRepository.save(ReviewItem(id = reviewId, question = question, user = user))
            }
        }

        return AnswerResponse(
            isCorrect = isCorrect,
            correctAnswer = question.correctAnswer,
            explanation = explanation,
        )
    }

    @Transactional
    fun addToReview(user: User, questionId: UUID) {
        reviewService.addToReview(user, questionId)
    }
}
