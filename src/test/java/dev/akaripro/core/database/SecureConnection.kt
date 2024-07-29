package dev.akaripro.core.database

import java.sql.Connection

class SecureConnectionTest {

    fun testThis() {
        val pop: Any = ""
        SecureConnection(pop as Connection).prepareStatement("")
    }
}