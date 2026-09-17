package org.example.quiz.repository

import org.example.quiz.entity.Question
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QuestionRepository : JpaRepository<Question, UUID> {
    fun findByQuizId(quizId: UUID): List<Question>
    fun countByQuizId(quizId: UUID): Long
}
