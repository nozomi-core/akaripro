package dev.akaripro.core.database

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
        invokeOnCreateDatabase(akariDb, builder.callbacks!!)
    }

    return akariDb
}

private fun invokeOnCreateDatabase(db: AkariDb, callback: DatabaseCallbacks) {
    initAkariDatabaseSchema(db)
    callback.onCreate(db)
}

private fun initAkariDatabaseSchema(db: AkariDb) {
    val smt = db.connection.createStatement()

    smt.execute("""
         create table ${AkariSchema.AkariEnv.table}(
            ${AkariSchema.AkariEnv.key} TEXT PRIMARY KEY ON CONFLICT REPLACE,
            ${AkariSchema.AkariEnv.value} TEXT,
            ${AkariSchema.AkariEnv.type} TEXT
         );
    """)
}

