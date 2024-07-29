package dev.akaripro.core.database

interface MigrationHandler {
    fun onUpgrade(connection: SecureConnection)
}

class SecureMigration {
    private val migrateMap = HashMap<Int, MigrationHandler>()

    fun add(toVersion: Int, migrationHandler: MigrationHandler) {
        /* TODO: ensure that
            1. There is no duplicate 'version' code in the list of migrations. Only 1 migration per int
            2. There can not be duplicate class handlers. Each migration should have a different class.
        */
        migrateMap.put(toVersion, migrationHandler)
    }

    internal fun runMigration(connection: SecureConnection, version: Int) {
        //TODO: handle if not migration is found
        migrateMap[version]!!.onUpgrade(connection)
    }
}
