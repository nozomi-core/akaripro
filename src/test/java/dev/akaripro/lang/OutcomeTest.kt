package dev.akaripro.lang

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.CompletableFuture

class OutcomeTest {

    @Test
    fun testThenGetOrNull() {
        val length = Outcome.ok("name").then {
            it.length
        }.getOrNull()
        assertEquals(4, length)
    }

    @Test
    fun testThenIfOk() {
        var length: Int? = null

        Outcome.ok("hello-world").then {
            it.length
        }.ifOk {
            length = it
        }
        assertEquals(11, length)
    }

    @Test
    fun testThenIfFail() {
        var ex: Exception? = null

        Outcome.ok("").then {
            throw Exception("empty")
        }.ifFail { _, reason ->
           ex = reason.parent
        }
        assertNotNull(ex)
        assertEquals("empty", ex?.message)
    }

    @Test
    fun testThenSuspend() {
        val future = CompletableFuture<Int?>()

        GlobalScope.launch {
            val result = Outcome.ok("suspend").thenSuspend {
                delay(100)
                it.length
            }.getOrNull()
            future.complete(result)
        }
        assertEquals(7, future.get())
    }
}