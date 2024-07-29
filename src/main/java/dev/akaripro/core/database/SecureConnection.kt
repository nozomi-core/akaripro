package dev.akaripro.core.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

class SecureConnection(private val connection: Connection) {

    fun createStatement(): Statement {
        return connection.createStatement()
    }

    fun prepareStatement(sql: String): PreparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
}