package org.example.quiz.repository

import org.example.quiz.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByGoogleId(googleId: String): User?
    fun findByEmail(email: String): User?
}