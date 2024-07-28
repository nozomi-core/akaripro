package dev.akaripro.core.database

import dev.akaripro.lang.Outcome
import java.io.File
import java.sql.DriverManager

fun createAkariDatabase(callback: AkariDbBuilder.() -> Unit): AkariDb {
    val builder = AkariDbBuilder()
    callback(builder)

    val dbType = builder.type
    val dbName = builder.name

    val dbState: DatabaseState = when(dbType) {
        is DatabaseType.FileDatabase -> {
            val dbFile = File(dbType.parentDir, dbName)
            val firstTimeCreated = !dbFile.exists()

            dbFile.parentFile.mkdirs()

            val connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
            if(firstTimeCreated)
                DatabaseState.Empty(connection)
            else
                DatabaseState.Reconnect(connection)

        }
        is DatabaseType.InMemory -> {
            val connection = DriverManager.getConnection("jdbc:sqlite:memory:${dbName!!}")
            DatabaseState.Empty(connection)
        }
        else -> { throw Exception() }
    }

    val akariDb = AkariDb(dbState.getDbConnection())

    if(dbState is DatabaseState.Empty) {
        val initDb = invokeOnCreateDatabase(akariDb, builder.callbacks)
        initDb.ifFail { _, _ ->
            akariDb.close()
            cleanupDbCreateFailed(builder)
        }
    }

    return akariDb
}

private fun cleanupDbCreateFailed(builder: AkariDbBuilder) {
    when(val dbType = builder.type) {
        is DatabaseType.FileDatabase -> {
            val dbFile = File(dbType.parentDir, builder.name)
            dbFile.delete()
        }
        else -> {}
    }
}

private fun invokeOnCreateDatabase(db: AkariDb, callback: DatabaseCallbacks): Outcome<Unit> {
    return initAkariDb(db).then {
        val userOnCreateSQL = db.useTransaction { connection ->
            val secureConnection = SecureConnection(connection)

            callback.onCreate(secureConnection)
            connection.createStatement()
                .execute("insert into ${AkariSchema.AkariEnv.table}(key, value, type) values ('db_version', '1', 'int')")
        }

        //Crash if users onCreate SQL does not return success, this is to stop corruption of data
        userOnCreateSQL.requireOkOrThrow()
    }
}

