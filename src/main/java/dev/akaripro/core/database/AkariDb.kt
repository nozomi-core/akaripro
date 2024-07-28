package dev.akaripro.core.database

import dev.akaripro.lang.Outcome
import java.sql.Connection

class AkariDb(
    private val connection: Connection
) {
    init {
        connection.autoCommit = false
    }

    fun useTransaction(callback: (Connection) -> Unit): Outcome<Unit> {
        return try {
            callback(connection)
            connection.commit()
            Outcome.ok(Unit)
        } catch (e: Exception) {
            connection.rollback()
            Outcome.fail(e)
        }
    }

    fun close() {
        connection.close()
    }
}