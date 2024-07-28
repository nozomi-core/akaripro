package dev.akaripro.core.database

interface DatabaseCallbacks {
    fun onCreate(connection: SecureConnection)
    fun onUpgrade(connection: SecureConnection, toVersion: Int)
}