package dev.akaripro.core.database

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
            connection.createStatement().executeQuery("select * from ${AkariSchema.AkariEnv.table} where ${AkariSchema.AkariEnv.key}= '${AkariSchema.AkariEnv.Keys.DB_VERSION}'")
        }.then {
            it.getString(AkariSchema.AkariEnv.value).toInt()
        }
    }
}