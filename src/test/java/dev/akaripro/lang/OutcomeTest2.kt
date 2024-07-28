package dev.akaripro.lang

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OutcomeTest2 {

    @Test
    fun test() {
        var fullString: String? = null
        create()
            .pipe { addAbc(it) }
            .next { add123(it) }
            .next()
            .then {
                fullString = it
            }
        assertEquals("start-abc123", fullString)
    }

    private fun create(): Outcome<String> {
        return Outcome.ok("start-")
    }

    private fun addAbc(inp: Outcome<String>): Outcome<String> {
        return inp.then {
            it.plus("abc")
        }
    }

    private fun add123(inp: Outcome<String>): Outcome<String> {
        return inp.then {
            it.plus("123")
        }
    }
}