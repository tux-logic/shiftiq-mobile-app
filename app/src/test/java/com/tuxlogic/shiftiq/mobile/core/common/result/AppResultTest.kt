package com.tuxlogic.shiftiq.mobile.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun `success result contains data and isSuccess is true`() {
        val result: AppResult<String> = AppResult.Success("hello")

        assertTrue(result.isSuccess)
        assertEquals("hello", result.getOrNull())
    }

    @Test
    fun `failure result contains error and isFailure is true`() {
        val error = AppError.NotFound(message = "Not found")
        val result: AppResult<String> = AppResult.Failure(error)

        assertTrue(result.isFailure)
        assertNull(result.getOrNull())
    }

    @Test
    fun `map transforms data when success`() {
        val result: AppResult<Int> = AppResult.Success(10)
        val mapped = result.map { it * 2 }

        assertEquals(20, mapped.getOrNull())
    }
}
