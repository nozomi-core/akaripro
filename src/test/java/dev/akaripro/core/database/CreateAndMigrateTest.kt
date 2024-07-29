package dev.akaripro.core.database

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class MigrateVersion2(val callback: (Int) -> Unit): MigrationHandler {
    override fun onUpgrade(connection: SecureConnection) {
        callback(2)
        connection.createStatement().execute("create table migrate2(name TEXT);")
    }
}

class MigrateVersion3(val callback: (Int) -> Unit): MigrationHandler {
    override fun onUpgrade(connection: SecureConnection) {
        callback(3)
        connection.createStatement().execute("create table migrate3(age INTEGER);")
    }
}

class CreateAkariDbTest {

    //TODO: add tests to test if init db fails. File should not be created
    @Test
    fun testCreateDatabase() {
        val testDir = ".test"
        val dbName = "create_akari_success.db"

        val db = createAkariDatabase {
            version = 1
            name = dbName
            type = DatabaseType.FileDatabase(File(testDir))
            callbacks = object: DatabaseCallbacks {
                override fun onCreate(connection: SecureConnection) {
                    connection.createStatement().execute("create table anime(name TEXT);")
                }

                override fun onMigrate(migrations: SecureMigration) {}
            }
        }
        db.close()
        File(testDir, dbName).delete()
    }

    @Test
    fun testMigrateTov3() {
        val migrateString = StringBuilder()
        val testDir = ".test"
        val dbName = "create_migrate_success.db"



        val db = createAkariDatabase {
            version = 3
            name = dbName
            type = DatabaseType.FileDatabase(File(testDir))
            callbacks = object: DatabaseCallbacks {
                override fun onCreate(connection: SecureConnection) {
                    connection.createStatement().execute("create table anime(name TEXT);")
                }

                override fun onMigrate(migrations: SecureMigration) {
                    migrations.add(2, MigrateVersion2 {
                        migrateString.append("$it,")
                    })
                    migrations.add(3, MigrateVersion3 {
                        migrateString.append("$it,")
                    })
                }
            }
        }
        val upgradedVersion = db.getVersion().getOrThrow()
        db.close()
        File(testDir, dbName).delete()
        assertEquals("2,3,", migrateString.toString())
        assertEquals(3, upgradedVersion)
    }
}