package com.tuxlogic.shiftiq.mobile.core.presentation.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.IOException

/** Writes a compact, paginated PDF to a URI selected with Android's document picker. */
object PdfDocumentExporter {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT_MARGIN = 48f
    private const val RIGHT_MARGIN = 48f
    private const val TOP_MARGIN = 52f
    private const val BOTTOM_MARGIN = 48f

    fun write(
        context: Context,
        destination: Uri,
        title: String,
        lines: List<String>
    ): Result<Unit> = runCatching {
        val document = PdfDocument()
        try {
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(27, 48, 105)
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(35, 39, 47)
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(105, 112, 125)
                textSize = 9f
            }
            val maxTextWidth = PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN
            var pageNumber = 0
            var page: PdfDocument.Page? = null
            var canvas: android.graphics.Canvas? = null
            var y = TOP_MARGIN

            fun startPage() {
                pageNumber += 1
                page = document.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                )
                canvas = page?.canvas
                y = TOP_MARGIN
                canvas?.drawText(title, LEFT_MARGIN, y, titlePaint)
                y += 28f
                canvas?.drawLine(
                    LEFT_MARGIN,
                    y,
                    PAGE_WIDTH - RIGHT_MARGIN,
                    y,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.rgb(213, 219, 230)
                        strokeWidth = 1f
                    }
                )
                y += 24f
            }

            fun finishPage() {
                val current = page ?: return
                canvas?.drawText(
                    "ShiftIQ  •  Página $pageNumber",
                    LEFT_MARGIN,
                    PAGE_HEIGHT - 22f,
                    footerPaint
                )
                document.finishPage(current)
                page = null
                canvas = null
            }

            fun drawTextLine(line: String) {
                if (line.isBlank()) {
                    y += 10f
                    return
                }
                val words = line.trim().split(Regex("\\s+"))
                var wrapped = ""
                for (word in words) {
                    val candidate = if (wrapped.isEmpty()) word else "$wrapped $word"
                    if (bodyPaint.measureText(candidate) > maxTextWidth && wrapped.isNotEmpty()) {
                        if (y > PAGE_HEIGHT - BOTTOM_MARGIN) {
                            finishPage()
                            startPage()
                        }
                        canvas?.drawText(wrapped, LEFT_MARGIN, y, bodyPaint)
                        y += 17f
                        wrapped = word
                    } else {
                        wrapped = candidate
                    }
                }
                if (y > PAGE_HEIGHT - BOTTOM_MARGIN) {
                    finishPage()
                    startPage()
                }
                if (wrapped.isNotEmpty()) {
                    canvas?.drawText(wrapped, LEFT_MARGIN, y, bodyPaint)
                    y += 17f
                }
            }

            startPage()
            lines.forEach(::drawTextLine)
            finishPage()

            val output = context.contentResolver.openOutputStream(destination, "w")
                ?: throw IOException("No se pudo abrir el archivo PDF para escritura.")
            output.use { stream -> document.writeTo(stream) }
        } finally {
            document.close()
        }
    }
}
