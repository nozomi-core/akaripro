package dev.akaripro.core.database

import java.sql.Connection

class AkariDb(
    val connection: Connection
) {
    fun close() {
        connection.close()
    }
}