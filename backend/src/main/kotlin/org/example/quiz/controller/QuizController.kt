package org.example.quiz.controller

import org.example.quiz.config.CurrentUserResolver
import org.example.quiz.dto.DeleteQuizCheckResponse
import org.example.quiz.dto.QuestionDto
import org.example.quiz.dto.QuizDto
import org.example.quiz.repository.QuestionRepository
import org.example.quiz.repository.QuizRepository
import org.example.quiz.service.QuizService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/quizzes")
class QuizController(
    private val quizService: QuizService,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val currentUserResolver: CurrentUserResolver,
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun createQuiz(
        @AuthenticationPrincipal principal: OAuth2User?,
        @RequestParam name: String,
        @RequestParam file: MultipartFile,
        @RequestParam closedCount: Int,
        @RequestParam openCount: Int,
    ): QuizDto {
        val user = currentUserResolver.resolve(principal)
        require(closedCount + openCount > 0) { "Łączna liczba pytań musi być większa od 0" }
        return quizService.createQuiz(user, name, file, closedCount, openCount)
    }

    @GetMapping
    fun listQuizzes(@AuthenticationPrincipal principal: OAuth2User?): List<QuizDto> {
        val user = currentUserResolver.resolve(principal)
        return quizService.listQuizzes(user)
    }

    @GetMapping("/{id}/questions")
    fun getQuestions(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable id: UUID,
    ): List<QuestionDto> {
        val user = currentUserResolver.resolve(principal)
        val quiz = quizRepository.findById(id).orElseThrow { NoSuchElementException("Quiz nie istnieje") }
        require(quiz.user.id == user.id) { "Brak dostępu" }
        return questionRepository.findByQuizId(id).map { QuestionDto.from(it) }
    }

    @DeleteMapping("/{id}/check")
    fun checkDelete(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable id: UUID,
    ): DeleteQuizCheckResponse {
        val user = currentUserResolver.resolve(principal)
        return quizService.checkDelete(user, id)
    }

    @DeleteMapping("/{id}")
    fun deleteQuiz(
        @AuthenticationPrincipal principal: OAuth2User?,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        val user = currentUserResolver.resolve(principal)
        quizService.deleteQuiz(user, id)
        return ResponseEntity.noContent().build()
    }
}