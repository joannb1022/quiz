package org.example.quiz.controller

import org.example.quiz.config.CurrentUserResolver
import org.example.quiz.dto.UserDto
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal

@RestController
@RequestMapping("/api/v1/user")
class UserController(private val currentUserResolver: CurrentUserResolver) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: OAuth2User?): UserDto {
        val user = currentUserResolver.resolve(principal)
        return UserDto.from(user)
    }
}