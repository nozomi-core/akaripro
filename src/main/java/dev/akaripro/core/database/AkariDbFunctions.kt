package dev.akaripro.core.database

import dev.akaripro.core.database.schema.AkariEnv
import dev.akaripro.lang.Outcome
import dev.akaripro.lang.RequireOk

internal fun initAkariDb(db: AkariDb): Outcome<RequireOk> {
    val akariOnCreateSql = db.useTransaction { connection ->
        val stmt = connection.createStatement()
        stmt.execute(AkariEnv.Statements.createTable())
        stmt.execute(AkariEnv.Statements.insertDbVersion(0))
    }

    //We must fail the database if akari db does not init. This ensures further corruption will not occur.
    return akariOnCreateSql.requireOkOrThrow()
}