package org.example.quiz.service

import org.example.quiz.dto.ReviewQuestionDto
import org.example.quiz.entity.ReviewItem
import org.example.quiz.entity.ReviewItemId
import org.example.quiz.entity.User
import org.example.quiz.repository.QuestionRepository
import org.example.quiz.repository.ReviewItemRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReviewService(
    private val reviewItemRepository: ReviewItemRepository,
    private val questionRepository: QuestionRepository,
) {

    fun getReviewItems(user: User): List<ReviewQuestionDto> {
        return reviewItemRepository.findByUserId(user.id)
            .map { ReviewQuestionDto.from(it.question) }
    }

    @Transactional
    fun addToReview(user: User, questionId: UUID) {
        val question = questionRepository.findById(questionId)
            .orElseThrow { NoSuchElementException("Pytanie nie istnieje") }
        val id = ReviewItemId(questionId = question.id, userId = user.id)
        if (!reviewItemRepository.existsById(id)) {
            reviewItemRepository.save(ReviewItem(id = id, question = question, user = user))
        }
    }

    @Transactional
    fun removeFromReview(user: User, questionId: UUID) {
        reviewItemRepository.deleteByUserIdAndQuestionId(user.id, questionId)
    }
}
