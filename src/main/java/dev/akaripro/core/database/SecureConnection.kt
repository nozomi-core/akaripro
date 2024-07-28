package dev.akaripro.core.database

import java.sql.Connection
import java.sql.PreparedStatement

class SecureConnection(private val connection: Connection) {
    fun prepareStatement(sql: String): PreparedStatement {
        return connection.prepareStatement(sql)
    }
}