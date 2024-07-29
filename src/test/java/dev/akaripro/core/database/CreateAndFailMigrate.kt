package dev.akaripro.core.database

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class MigrateVersionFail2(val callback: (Int) -> Unit): MigrationHandler {
    override fun onUpgrade(connection: SecureConnection) {
        callback(2)
        connection.createStatement().execute("create table migrate2(name TEXT);")
    }
}

class MigrateVersionFail3(val callback: (Int) -> Unit): MigrationHandler {
    override fun onUpgrade(connection: SecureConnection) {
        callback(3)
    }
}

class CreateAkariDbTestFail {

    @Test
    fun testMigrateTov3() {
        val migrateString = StringBuilder()
        val testDir = ".test"
        val dbName = "create_akari_migrate_fail.db"

        val file = File(testDir, dbName)

        val db = createAkariDatabase {
            version = 3
            name = dbName
            type = DatabaseType.FileDatabase(File(testDir))
            callbacks = object: DatabaseCallbacks {
                override fun onCreate(connection: SecureConnection) {
                    connection.createStatement().execute("create table anime(name TEXT);")
                }

                override fun onMigrate(migrations: SecureMigration) {
                    migrations.add(2, MigrateVersionFail2 {
                        migrateString.append("$it,")
                    })
                    migrations.add(3, MigrateVersionFail3 {
                        migrateString.append("$it,")
                    })
                }
            }
        }
        val upgradedVersion = db.getVersion().getOrThrow()
        db.close()
        file.delete()
        assertEquals("2,3,", migrateString.toString())
        assertEquals(3, upgradedVersion)
    }
}