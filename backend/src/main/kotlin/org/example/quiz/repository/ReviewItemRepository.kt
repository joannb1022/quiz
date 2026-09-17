package org.example.quiz.repository

import org.example.quiz.entity.ReviewItem
import org.example.quiz.entity.ReviewItemId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ReviewItemRepository : JpaRepository<ReviewItem, ReviewItemId> {
    fun findByUserId(userId: UUID): List<ReviewItem>

    @Query("""
        SELECT COUNT(ri) > 0 FROM ReviewItem ri
        JOIN ri.question q
        WHERE ri.user.id = :userId AND q.quiz.id = :quizId
    """)
    fun existsByUserIdAndQuizId(userId: UUID, quizId: UUID): Boolean

    fun deleteByUserIdAndQuestionId(userId: UUID, questionId: UUID)

    @Query("""
        DELETE FROM ReviewItem ri WHERE ri.question.quiz.id = :quizId AND ri.user.id = :userId
    """)
    @Modifying
    fun deleteByQuizIdAndUserId(quizId: UUID, userId: UUID)
}
