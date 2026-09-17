package org.example.quiz.service

import org.example.quiz.dto.UsersSummaryDto
import org.example.quiz.repository.QuizRepository
import org.example.quiz.repository.UserAnswerRepository
import org.example.quiz.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

@Service
class AdminDashboardService(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val userAnswerRepository: UserAnswerRepository
) {

    fun getUsersSummary(page: Int, size: Int): Page<UsersSummaryDto> {
        val pageable = PageRequest.of(page, size, Sort.by("email").ascending())
        return userRepository.findAll(pageable).map { user ->
            UsersSummaryDto(
                userId = user.id,
                email = user.email,
                roles = user.roles,
                quizCount = quizRepository.countByUserId(user.id),
                averageScore = calculateAverageScore(user.id)
            )
        }
    }

    private fun calculateAverageScore(userId: UUID): Double {
        val allAnswers = userAnswerRepository.countByUserId(userId)
        if (allAnswers == 0) return 0.0

        val correctAnswers = userAnswerRepository.countByUserIdAndIsCorrect(userId, true)
        val percent = (correctAnswers.toDouble() / allAnswers.toDouble()) * 100.0

        return BigDecimal(percent)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()
    }
}