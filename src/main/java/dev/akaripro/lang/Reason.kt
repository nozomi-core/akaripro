package dev.akaripro.lang

class Reason private constructor(
    val reasonId: Int,
    val parent: Exception?
): Exception() {

    companion object {
        fun empty(): Reason {
            TODO()
        }

        fun exception(e: Exception): Reason {
            return Reason(0, e)
        }

        fun of(id: Int): Reason {
            TODO()
        }
    }
}