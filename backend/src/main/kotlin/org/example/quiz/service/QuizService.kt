package org.example.quiz.service

import org.example.quiz.dto.DeleteQuizCheckResponse
import org.example.quiz.dto.QuizDto
import org.example.quiz.entity.Question
import org.example.quiz.entity.QuestionType
import org.example.quiz.entity.Quiz
import org.example.quiz.entity.User
import org.example.quiz.repository.QuestionRepository
import org.example.quiz.repository.QuizRepository
import org.example.quiz.repository.ReviewItemRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val reviewItemRepository: ReviewItemRepository,
    private val pdfService: PdfService,
    private val geminiService: GeminiService,
) {

    @Transactional
    fun createQuiz(user: User, name: String, pdfFile: MultipartFile, closedCount: Int, openCount: Int): QuizDto {
        val text = pdfService.extractText(pdfFile)
        val generatedQuestions = geminiService.generateQuestions(text, closedCount, openCount)

        val quiz = quizRepository.save(Quiz(name = name, user = user))
        val questions = generatedQuestions.map { gq ->
            Question(
                quiz = quiz,
                content = gq.content,
                type = QuestionType.valueOf(gq.type.uppercase()),
                options = gq.options,
                correctAnswer = gq.correctAnswer,
                explanation = gq.explanation,
            )
        }
        questionRepository.saveAll(questions)

        return QuizDto.from(quiz, questions.size.toLong())
    }

    fun listQuizzes(user: User): List<QuizDto> {
        return quizRepository.findByUserIdOrderByCreatedAtDesc(user.id).map { quiz ->
            QuizDto.from(quiz, questionRepository.countByQuizId(quiz.id))
        }
    }

    fun checkDelete(user: User, quizId: UUID): DeleteQuizCheckResponse {
        val quiz = quizRepository.findById(quizId).orElseThrow { NoSuchElementException("Quiz nie istnieje") }
        require(quiz.user.id == user.id) { "Brak dostępu" }
        val hasReviewItems = reviewItemRepository.existsByUserIdAndQuizId(user.id, quizId)
        return DeleteQuizCheckResponse(hasReviewItems)
    }

    @Transactional
    fun deleteQuiz(user: User, quizId: UUID) {
        val quiz = quizRepository.findById(quizId).orElseThrow { NoSuchElementException("Quiz nie istnieje") }
        require(quiz.user.id == user.id) { "Brak dostępu" }
        quizRepository.delete(quiz)
    }
}
