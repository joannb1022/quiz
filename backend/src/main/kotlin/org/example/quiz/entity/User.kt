package org.example.quiz.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "google_id", nullable = false, unique = true)
    val googleId: String,

    @Column(nullable = false)
    val email: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Enumerated(EnumType.STRING) // so it's not saved as 1, 2, 3
    @Column(name = "role")
    var roles: MutableSet<Role> = mutableSetOf(Role.USER)
)


enum class Role {
    USER,
    ADMIN
}