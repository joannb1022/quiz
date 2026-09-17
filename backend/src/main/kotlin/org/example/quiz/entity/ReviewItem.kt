package org.example.quiz.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Embeddable
data class ReviewItemId(
    val questionId: UUID,
    val userId: UUID,
) : Serializable

@Entity
@Table(name = "review_items")
class ReviewItem(
    @EmbeddedId
    val id: ReviewItemId,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    val question: Question,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "added_at", nullable = false, updatable = false)
    val addedAt: Instant = Instant.now(),
)
