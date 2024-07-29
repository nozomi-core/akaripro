package dev.akaripro.lang

sealed class Outcome<out T> {
    class Ok<out T> internal constructor(val value: T): Outcome<T>()
    class Fail internal constructor(val reason: Reason): Outcome<Nothing>()

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

    fun requireOkOrThrow(): Outcome<RequireOk> {
        if(this is Fail) {
            throw this.reason.exception
        } else {
            return ok(RequireOk())
        }
    }

    fun getOrThrow(): T {
        return when(this) {
            is Fail -> throw this.reason.exception
            is Ok -> this.value
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

        fun <T> tryThis(callback: () -> T): Outcome<T> {
            return try {
                ok(callback())
            } catch (e: Exception) {
                fail(e)
            }
        }
    }
}

fun <Y> Outcome<Outcome<Y>>.next(): Outcome<Y> {
    return getOrNull() ?: Outcome.fail(Exception())
}

fun <T, N> Outcome<Outcome<T>>.next(mapper: (input: Outcome<T>) -> N): Outcome<N> {
    val nextValue = next()
    return nextValue.pipe(mapper)
}

class RequireOk internal constructor()