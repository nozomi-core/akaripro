package dev.akaripro.core.database

import java.sql.Connection

sealed class DatabaseState {
    class Empty(val connection: Connection): DatabaseState()
    class Reconnect(val connection: Connection): DatabaseState()

    fun getDbConnection(): Connection {
        return when(this) {
            is Empty -> this.connection
            is Reconnect -> this.connection
        }
    }
}