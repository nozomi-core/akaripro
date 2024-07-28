package dev.akaripro.lang

sealed class Outcome<out T> {
    class Ok<out T> internal constructor(val value: T): Outcome<T>()
    class Fail internal constructor(val reason: Reason): Outcome<Nothing>()

    //Maps an outcome functionally to another one.
    fun <N> then(mapper: (input: T) -> N): Outcome<N> {
        return when(this) {
            is Ok -> {
                try {
                    ok(mapper(value))
                } catch (e: Exception) {
                    fail(e)
                }
            }
            is Fail -> this
        }
    }

    suspend fun <N> thenSuspend(mapper: suspend (input: T) -> N): Outcome<N> {
        return when(this) {
            is Ok -> {
                try {
                    ok(mapper(value))
                } catch (e: Exception) {
                    fail(e)
                }
            }
            is Fail -> this
        }
    }

    fun ifFail(callback: (Int, Reason) -> Unit): Outcome<T> {

        return this
    }

    fun ifOk(callback: (T) -> Unit): Outcome<T> {

        return this
    }

    fun getOrNull(): T? {
        return when(this) {
            is Ok -> this.value
            else -> null
        }
    }

    companion object {

        fun <T> ok(value: T): Outcome<T> {
            return if(value == null) {
                Fail(Reason.empty())
            } else {
                Ok(value)
            }
        }

        fun fail(e: Exception): Outcome<Nothing> {
            return Fail(Reason.exception(e))
        }
    }
}