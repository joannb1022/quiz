package org.example.quiz.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

data class GeneratedQuestion(
    val type: String,
    val content: String,
    val options: Map<String, String>? = null,
    val correctAnswer: String,
    val explanation: String? = null,
)

data class EvaluationResult(
    val isCorrect: Boolean,
    val explanation: String,
)

@Service
class GeminiService(
    @Value("\${google.gemini.api-key}") private val apiKey: String,
    @Value("\${google.gemini.model}") private val model: String,
) {
    private val client = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .defaultHeader("content-type", "application/json")
        .build()

    private val mapper = jacksonObjectMapper()

    fun generateQuestions(text: String, closedCount: Int, openCount: Int): List<GeneratedQuestion> {
        val prompt = buildGenerationPrompt(text, closedCount, openCount)
        val responseText = callGemini(prompt, SYSTEM_GENERATION, TEMPERATURE_GENERATION)
        return parseGeneratedQuestions(responseText)
    }

    fun evaluateOpenAnswer(question: String, correctAnswer: String, userAnswer: String): EvaluationResult {
        val prompt = buildEvaluationPrompt(question, correctAnswer, userAnswer)
        val responseText = callGemini(prompt, SYSTEM_EVALUATION, TEMPERATURE_EVALUATION)
        return parseEvaluation(responseText)
    }

    private fun callGemini(userMessage: String, systemMessage: String, temperature: Double): String {
        val body = mapOf(
            "system_instruction" to mapOf(
                "parts" to listOf(mapOf("text" to systemMessage))
            ),
            "contents" to listOf(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(mapOf("text" to userMessage))
                )
            ),
            "generationConfig" to mapOf(
                "temperature" to temperature,
                "maxOutputTokens" to MAX_OUTPUT_TOKENS,
                "topP" to 0.8,
                "topK" to 20,
                "responseMimeType" to "application/json",
            )
        )

        val response = client.post()
            .uri("/v1beta/models/$model:generateContent?key=$apiKey")
            .bodyValue(body)
            .retrieve()
            .bodyToMono<Map<String, Any>>()
            .block() ?: error("Brak odpowiedzi od Gemini API")

        @Suppress("UNCHECKED_CAST")
        val candidates = (response["candidates"] as? List<Map<String, Any>>)
            ?: error("Nieprawidłowy format odpowiedzi Gemini API")

        @Suppress("UNCHECKED_CAST")
        val content = (candidates.firstOrNull()?.get("content") as? Map<String, Any>)
            ?: error("Brak zawartości w odpowiedzi Gemini API")

        @Suppress("UNCHECKED_CAST")
        val parts = (content["parts"] as? List<Map<String, String>>)
            ?: error("Brak części w odpowiedzi Gemini API")

        return parts.firstOrNull()?.get("text")
            ?: error("Nieprawidłowy format odpowiedzi Gemini API")
    }

    private fun parseGeneratedQuestions(raw: String): List<GeneratedQuestion> {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Wrapper(val questions: List<GeneratedQuestion>)
        return mapper.readValue<Wrapper>(raw).questions  // extractJson już niepotrzebny
    }

    private fun parseEvaluation(raw: String): EvaluationResult {
        return mapper.readValue(raw)  // extractJson już niepotrzebny
    }

    private fun buildGenerationPrompt(text: String, closedCount: Int, openCount: Int) = """
        Na podstawie poniższego tekstu wygeneruj $closedCount pytań zamkniętych (jednokrotny wybór, 4 odpowiedzi A-D) oraz $openCount pytań otwartych.
        Opieraj się WYŁĄCZNIE na treści podanego tekstu. Nie dodawaj wiedzy spoza notatek.

        Zwróć TYLKO JSON w podanym formacie, bez żadnego tekstu przed ani po:
        {
          "questions": [
            {
              "type": "CLOSED",
              "content": "treść pytania",
              "options": {"A": "...", "B": "...", "C": "...", "D": "..."},
              "correctAnswer": "A",
              "explanation": "krótkie wyjaśnienie dlaczego ta odpowiedź jest poprawna"
            },
            {
              "type": "OPEN",
              "content": "treść pytania",
              "correctAnswer": "wzorcowa pełna odpowiedź",
              "explanation": "wyjaśnienie"
            }
          ]
        }

        Tekst notatek:
        $text
    """.trimIndent()

    private fun buildEvaluationPrompt(question: String, correctAnswer: String, userAnswer: String) = """
        Oceń odpowiedź studenta na poniższe pytanie.

        Pytanie: $question
        Wzorcowa odpowiedź: $correctAnswer
        Odpowiedź studenta: $userAnswer

        Zwróć TYLKO JSON:
        {"isCorrect": true/false, "explanation": "krótkie uzasadnienie po polsku (1-2 zdania)"}
    """.trimIndent()

    companion object {
        private const val TEMPERATURE_GENERATION = 0.3   // niska — trzymamy się faktów z notatek
        private const val TEMPERATURE_EVALUATION = 0.0   // zero — ocena musi być deterministyczna
        private const val MAX_OUTPUT_TOKENS = 4096

        private const val SYSTEM_GENERATION = """
            Jesteś generatorem pytań quizowych.
            Opieraj się wyłącznie na treści przekazanych notatek.
            Zwróć TYLKO poprawny JSON bez żadnego dodatkowego tekstu.
        """

        private const val SYSTEM_EVALUATION = """
            Jesteś asystentem oceniającym odpowiedzi studentów.
            Bądź merytoryczny i obiektywny.
            Zwróć TYLKO poprawny JSON.
        """
    }
}


/*
        generationConfig = generationConfig {
            temperature = 1.0f        // 0.0 = deterministyczny, 2.0 = bardzo kreatywny
            topK = 40                 // bierze pod uwagę K najbardziej prawdopodobnych tokenów
            topP = 0.95f              // próbkowanie jądrowe (0.0–1.0)
            maxOutputTokens = 1024    // max długość odpowiedzi (~750 słów)
            candidateCount = 1        // ile wariantów odpowiedzi zwrócić (zazwyczaj 1)
            stopSequences = listOf("KONIEC", "---") // model zatrzyma się gdy napotka ten string
            responseMimeType = "text/plain" // albo "application/json" jeśli chcesz JSON
        },
 */