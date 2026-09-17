package org.example.quiz.config

import org.example.quiz.entity.Role
import org.example.quiz.entity.User
import org.example.quiz.repository.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DataInitializer(
    private val userRepository: UserRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val adminEmail = "admin@example.com"

        if (userRepository.findByEmail(adminEmail) == null) {
            userRepository.save(
                User(
                    googleId = "seed-admin-google-id",
                    email = adminEmail,
                    roles = mutableSetOf(Role.ADMIN)
                )
            )
        }
    }
}