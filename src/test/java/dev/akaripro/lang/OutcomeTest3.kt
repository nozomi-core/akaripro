package dev.akaripro.lang

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OutcomeTest3 {

    @Test
    fun testFail() {
        var fullString = "empty"

        create()
            .pipe { addAbc(it) }
            .next { add123(it) }
            .next()
            .then {
                fullString = it
            }
        assertEquals("empty", fullString)
    }

    private fun create(): Outcome<String> {
        return Outcome.ok("fail-")
    }

    private fun addAbc(inp: Outcome<String>): Outcome<String> {
        return inp.then {
            it.plus("abc")
        }
    }

    private fun add123(inp: Outcome<String>): Outcome<String> {
        return inp.then {
            throw Exception()
        }
    }
}