package org.example.quiz.controller

import org.example.quiz.config.CurrentUserResolver
import org.example.quiz.dto.ReviewQuestionDto
import org.example.quiz.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/review")
class ReviewController(
    private val reviewService: ReviewService,
    private val currentUserResolver: CurrentUserResolver,
) {

    @GetMapping
    fun getReviewItems(@AuthenticationPrincipal principal: OAuth2User?): List<ReviewQuestionDto> {
        val user = currentUserResolver.resolve(principal)
        return reviewService.getReviewItems(user)
    }

    @DeleteMapping("/{questionId}")
    fun removeFromReview(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable questionId: UUID,
    ): ResponseEntity<Void> {
        val user = currentUserResolver.resolve(principal)
        reviewService.removeFromReview(user, questionId)
        return ResponseEntity.noContent().build()
    }
}