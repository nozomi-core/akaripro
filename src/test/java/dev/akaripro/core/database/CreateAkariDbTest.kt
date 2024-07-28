package dev.akaripro.core.database

import org.junit.jupiter.api.Test
import java.io.File

class CreateAkariDbTest {

    //TODO: add tests to test if init db fails. File should not be created
    @Test
    fun testCreateDatabase() {
        createAkariDatabase {
            name = "create_akari.db"
            type = DatabaseType.FileDatabase(File(".test"))
            callbacks = object: DatabaseCallbacks {
                override fun onCreate(connection: SecureConnection) {
                    connection.prepareStatement("create table anime(name TEXT);").execute()
                }

                override fun onUpgrade(connection: SecureConnection, toVersion: Int) {
                    TODO("Not yet implemented")
                }
            }
        }
    }
}