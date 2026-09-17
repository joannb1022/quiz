package org.example.quiz.config

import org.example.quiz.entity.User
import org.example.quiz.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class CurrentUserResolver(
    private val userRepository: UserRepository,
    @Value("\${app.security.enabled:true}") private val securityEnabled: Boolean,
) {

    fun resolve(principal: OAuth2User?): User {
        if (!securityEnabled || principal == null) {
            // Dev mode - zwróć/utwórz test usera
            return userRepository.findByEmail("test@example.com")
                ?: userRepository.save(User(googleId = "test-user", email = "test@example.com"))
        }

        // Prod mode - parsuj OAuth2
        val googleId = principal.getAttribute<String>("sub")!!
        return userRepository.findByGoogleId(googleId)
            ?: error("Authenticated user not found in database: $googleId")
    }
}