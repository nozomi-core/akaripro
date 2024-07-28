package dev.akaripro.lang

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OutcomeTest {

    @Test
    fun testThenGetOrNull() {
        val length = Outcome.ok("name").then {
            it.length
        }.getOrNull()
        assertEquals(4, length)
    }
}