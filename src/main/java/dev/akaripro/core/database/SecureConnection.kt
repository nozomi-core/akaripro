package dev.akaripro.core.database

import java.sql.Connection
import java.sql.Statement

class SecureConnection(private val connection: Connection) {

    fun createStatement(): Statement {
        return connection.createStatement()
    }
}