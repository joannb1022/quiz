package org.example.quiz.controller

import org.example.quiz.config.CurrentUserResolver
import org.example.quiz.dto.AnswerRequest
import org.example.quiz.dto.AnswerResponse
import org.example.quiz.service.SessionService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/questions")
class SessionController(
    private val sessionService: SessionService,
    private val currentUserResolver: CurrentUserResolver,
) {

    @PostMapping("/{id}/answer")
    fun submitAnswer(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable id: UUID,
        @RequestBody request: AnswerRequest,
    ): AnswerResponse {
        val user = currentUserResolver.resolve(principal)
        return sessionService.submitAnswer(user, id, request.answer)
    }

    @PostMapping("/{id}/review")
    fun addToReview(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable id: UUID,
    ) {
        val user = currentUserResolver.resolve(principal)
        // delegated to ReviewService via SessionController for convenience
        sessionService.addToReview(user, id)
    }
}
