package dev.akaripro.core.database

import dev.akaripro.lang.RequireOk
import dev.akaripro.lang.Outcome
import java.io.File
import java.sql.DriverManager

fun  createAkariDatabase(callback: AkariDbBuilder.() -> Unit): AkariDb {
    val builder = AkariDbBuilder()
    callback(builder)

    val dbType = builder.type
    val dbName = builder.name
    val dbVersion = builder.version
    val dbCallbacks = builder.callbacks

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
        val initDb = invokeOnCreateDatabase(akariDb, dbCallbacks)
        initDb.ifFail { _, _ ->
            akariDb.close()
            cleanupDbCreateFailed(builder)
        }
    }

    invokeOnMigrations(akariDb, dbVersion, dbCallbacks)

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

            callback.onCreate(connection)
            connection.createStatement()
                .execute("insert into ${AkariSchema.AkariEnv.table}(key, value, type) values ('db_version', '1', 'int')")
        }

        //Crash if users onCreate SQL does not return success, this is to stop corruption of data
        userOnCreateSQL.requireOkOrThrow()
    }
}

private fun invokeOnMigrations(db: AkariDb, version: Int, callbacks: DatabaseCallbacks): Outcome<RequireOk> {
    val currentVersion = db.getVersion().getOrThrow()

    //There is no need to update if the current version matches what's in the database environment file
    if(currentVersion == version) {
        return Outcome.ok(RequireOk())
    }

    val migration = SecureMigration()
    callbacks.onMigrate(migration)

    return db.useTransaction { connection ->
        for(i in (currentVersion + 1)..version) {
            migration.runMigration(connection, i)
        }

        //TODO: move these elsewhere. There are now 3 calls to manually update the version number
        connection.createStatement()
            .execute("insert into ${AkariSchema.AkariEnv.table}(key, value, type) values ('db_version', '${version}', 'int')")
    }.requireOkOrThrow()
}

