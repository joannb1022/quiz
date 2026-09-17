package org.example.quiz.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

class PdfExtractionException(message: String) : RuntimeException(message)

@Service
class PdfService {

    fun extractText(file: MultipartFile): String {
        val text = file.inputStream.use { stream ->
            PDDocument.load(stream).use { doc ->
                PDFTextStripper().getText(doc)
            }
        }
        if (text.trim().length < 100) {
            throw PdfExtractionException(
                "PDF nie zawiera warstwy tekstowej (prawdopodobnie skan). " +
                "Wgraj PDF z tekstem możliwym do zaznaczenia."
            )
        }
        return text.trim()
    }
}
