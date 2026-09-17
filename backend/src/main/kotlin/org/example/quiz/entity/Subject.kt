package org.example.quiz.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "subjects")
class Subject(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
)
