package org.example.quiz.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "user_answers")
class UserAnswer(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "user_answer", nullable = false, columnDefinition = "TEXT")
    val userAnswer: String,

    @Column(name = "is_correct", nullable = false)
    val isCorrect: Boolean,

    @Column(name = "answered_at", nullable = false, updatable = false)
    val answeredAt: Instant = Instant.now(),
)
