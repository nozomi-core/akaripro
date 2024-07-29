package dev.akaripro.lang

class Reason private constructor(
    val reasonId: Int,
    private val parent: Exception?
) {
    val exception: Exception get() = parent ?: Exception()

    companion object {
        fun empty(): Reason {
            TODO()
        }

        fun exception(e: Exception): Reason {
            return Reason(0, e)
        }
    }
}