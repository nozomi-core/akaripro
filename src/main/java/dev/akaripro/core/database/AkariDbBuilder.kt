package dev.akaripro.core.database

private val defaultCallbacks = object: DatabaseCallbacks {
    override fun onCreate(connection: SecureConnection) {}
    override fun onMigrate(migrations: SecureMigration) {}
}

class AkariDbBuilder {
    var name: String? = null
    var callbacks: DatabaseCallbacks = defaultCallbacks
    var type: DatabaseType? = null
    var version = 1
}