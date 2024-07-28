package dev.akaripro.lang

sealed class Outcome<out T> {
    class Ok<out T> internal constructor(val value: T): Outcome<T>()
    class Fail internal constructor(val reason: Reason): Outcome<Nothing>()

    //Maps an outcome functionally to another one.
    fun <N> then(mapper: (input: T) -> N): Outcome<N> {
        return when(this) {
            is Ok -> {
                try {
                    val result = mapper(value)
                    ok(result)
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
                    val result = mapper(value)
                    ok(result)
                } catch (e: Exception) {
                    fail(e)
                }
            }
            is Fail -> this
        }
    }

    fun ifFail(callback: (Int, Reason) -> Unit): Outcome<T> {
        if(this is Fail) {
            callback(this.reason.reasonId, this.reason)
        }
        return this
    }

    fun ifOk(callback: (T) -> Unit): Outcome<T> {
        if(this is Ok) {
            callback(this.value)
        }
        return this
    }

    fun getOrNull(): T? {
        return when(this) {
            is Ok -> this.value
            else -> null
        }
    }

    fun <N> pipe(mapper: (input: Outcome<T>) -> N): Outcome<N> {
        return when(this) {
            is Ok -> {
                try {
                    val result = mapper(this)
                    ok(result)
                } catch (e: Exception) {
                    fail(e)
                }
            }
            is Fail -> this
        }
    }

    companion object {

        fun <T> ok(value: T): Outcome<T> {
            return if(value == null) {
                Fail(Reason.empty())
            } else {
                Ok<T>(value)
            }
        }

        fun fail(e: Exception): Outcome<Nothing> {
            return Fail(Reason.exception(e))
        }
    }
}

fun <Y> Outcome<Outcome<Y>>.next(): Outcome<Y> {
    return getOrNull() ?: Outcome.fail(Reason.empty())
}

fun <T, N> Outcome<Outcome<T>>.next(mapper: (input: Outcome<T>) -> N): Outcome<N> {
    val nextValue = next()
    return nextValue.pipe(mapper)
}