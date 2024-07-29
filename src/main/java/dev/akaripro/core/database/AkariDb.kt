package dev.akaripro.core.database

import dev.akaripro.core.database.schema.AkariEnv
import dev.akaripro.lang.Outcome
import java.sql.Connection

class AkariDb(
    private val connection: Connection
) {
    init {
        connection.autoCommit = false
    }

    fun useTransaction(callback: (SecureConnection) -> Unit): Outcome<Unit> {
        return try {
            callback(SecureConnection(connection))
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

    fun getVersion(): Outcome<Int> {
        return Outcome.tryThis {
            connection.createStatement().executeQuery(AkariEnv.Queries.selectDbVersion)
        }.then {
            it.getString(AkariEnv.value).toInt()
        }
    }
}