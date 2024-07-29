package dev.akaripro.core.database

interface DatabaseCallbacks {
    fun onCreate(connection: SecureConnection)
    fun onMigrate(migrations: SecureMigration)
}