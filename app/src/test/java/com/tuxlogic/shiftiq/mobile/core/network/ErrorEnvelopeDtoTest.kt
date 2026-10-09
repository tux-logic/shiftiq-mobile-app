package com.tuxlogic.shiftiq.mobile.core.network

import com.google.gson.Gson
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.network.dto.ErrorEnvelopeDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorEnvelopeDtoTest {

    private val gson = Gson()

    @Test
    fun `parses standard ShiftIQ error envelope correctly`() {
        val json = """
            {
              "code": "BAD_REQUEST",
              "message": "Field plateNumber is required",
              "details": "Validation failed for request body"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ErrorEnvelopeDto::class.java)
        val appError = dto.toAppError(400)

        assertTrue(appError is AppError.Validation)
        assertEquals("Field plateNumber is required", appError.message)
    }

    @Test
    fun `parses Spring ProblemDetail fallback correctly`() {
        val json = """
            {
              "type": "about:blank",
              "title": "Unprocessable Content",
              "status": 422,
              "detail": "Unsupported vehicle status filter"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ErrorEnvelopeDto::class.java)
        val appError = dto.toAppError(409)

        assertTrue(appError is AppError.Conflict)
        assertEquals("Unsupported vehicle status filter", appError.message)
    }
}
