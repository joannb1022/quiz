package org.example.quiz.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

enum class QuestionType { CLOSED, OPEN }

@Entity
@Table(name = "questions")
class Question(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    val quiz: Quiz,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val type: QuestionType,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val options: Map<String, String>? = null,

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    val correctAnswer: String,

    @Column(columnDefinition = "TEXT")
    val explanation: String? = null,
)
