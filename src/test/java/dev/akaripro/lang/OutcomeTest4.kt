package dev.akaripro.lang

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OutcomeTest4 {

    @Test
    fun testMultiFunctionalCalls() {
        val message = Outcome.ok("  CREATE table   ")
        val trim = doRemoveWhitespace(message)
        val execute = doDatabase(trim)
        val check = doCheckUpdate(execute)
        val result = check.getOrNull()
        assertTrue(result!!)
    }

    @Test
    fun testMultiFunctionalCallWithPipes() {
        var result: Boolean? = null

        Outcome.ok("  CREATE table   ")
            .pipe { doRemoveWhitespace(it) }
            .next { doDatabase(it) }
            .next { doCheckUpdate(it) }
            .next()
            .then {
                result = it
            }

        assertTrue(result!!)
    }

    private fun doRemoveWhitespace(action: Outcome<String>): Outcome<String> {
        return action.then {
            it.trim()
        }
    }

    private fun doDatabase(action: Outcome<String>): Outcome<String> {
        return action.then {
            if(it == "CREATE table")
                "updated"
            else
                "failed"
        }
    }
    private fun doCheckUpdate(action: Outcome<String>): Outcome<Boolean> {
        return action.then {
           it == "updated"
        }
    }
}