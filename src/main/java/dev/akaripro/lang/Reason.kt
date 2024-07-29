package dev.akaripro.lang

class Reason private constructor(
    val reasonId: Int,
    private val parent: Exception?
) {
    val exception: Exception get() = parent ?: Exception()

    companion object {
        fun empty(): Reason {
            return Reason(0,Exception())
        }

        fun exception(e: Exception): Reason {
            return Reason(1, e)
        }
    }
}