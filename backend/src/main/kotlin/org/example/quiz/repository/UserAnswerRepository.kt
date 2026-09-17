package org.example.quiz.repository

import org.example.quiz.entity.UserAnswer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAnswerRepository : JpaRepository<UserAnswer, UUID> {
    fun findByUserIdAndQuestionId(userId: UUID, questionId: UUID): UserAnswer?
}
