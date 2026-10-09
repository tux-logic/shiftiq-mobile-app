package com.tuxlogic.shiftiq.mobile.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class ModelTest {

    @Test
    fun `Role fromName parses correctly with and without ROLE_ prefix`() {
        assertEquals(Role.ROLE_OWNER, Role.fromName("ROLE_OWNER"))
        assertEquals(Role.ROLE_OWNER, Role.fromName("owner"))
        assertEquals(Role.ROLE_EMPLOYEE, Role.fromName("ROLE_EMPLOYEE"))
        assertNull(Role.fromName("UNKNOWN_ROLE"))
    }

    @Test
    fun `Role hierarchy check works properly`() {
        assertTrue(Role.ROLE_ADMIN.hasAtLeast(Role.ROLE_OWNER))
        assertTrue(Role.ROLE_OWNER.hasAtLeast(Role.ROLE_BRANCH_MANAGER))
        assertFalse(Role.ROLE_EMPLOYEE.hasAtLeast(Role.ROLE_ASSISTANT))
    }

    @Test
    fun `Money operations calculate correctly`() {
        val m1 = Money(100.50)
        val m2 = Money(50.25)

        val sum = m1 + m2
        assertEquals(BigDecimal("150.75"), sum.amount)

        val diff = m1 - m2
        assertEquals(BigDecimal("50.25"), diff.amount)
    }
}
