package org.example.quiz.repository

import org.example.quiz.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QuizRepository : JpaRepository<Quiz, UUID> {
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<Quiz>
}
