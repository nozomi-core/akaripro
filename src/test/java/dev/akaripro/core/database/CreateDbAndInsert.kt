package dev.akaripro.core.database

import org.junit.jupiter.api.Test
import java.io.File

class CreateDbAndInsert {

    @Test
    fun testCreateAndInsert() {
        val testDir = ".test"
        val dbName = "create_akari_insert_success.db"

        val db = createAkariDatabase {
            version = 1
            name = dbName
            type = DatabaseType.FileDatabase(File(testDir))
            callbacks = object: DatabaseCallbacks {
                override fun onCreate(connection: SecureConnection) {
                    connection.createStatement().execute("create table my_table(name TEXT);")
                }

                override fun onMigrate(migrations: SecureMigration) {}
            }
        }
        db.useTransaction { connection ->
            connection.createStatement().execute("insert into my_table(name) values('testing this');")
        }.requireOkOrThrow()

        db.close()
        File(testDir, dbName).delete()
    }
}