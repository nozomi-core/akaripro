package dev.akaripro.core.database

import dev.akaripro.lang.Outcome

internal fun initAkariDb(db: AkariDb): Outcome<Unit> {
    val akariOnCreateSql = db.useTransaction { connection ->
        val smt = connection.createStatement()

        smt.execute("""
            create table ${AkariSchema.AkariEnv.table}(
                ${AkariSchema.AkariEnv.key} TEXT PRIMARY KEY ON CONFLICT REPLACE,
                ${AkariSchema.AkariEnv.value} TEXT,
                ${AkariSchema.AkariEnv.type} TEXT
            );
         """)

        //TODO: move into a repo, this file shoduld only create the tables. This is because api should auto fill the type
        smt.execute("insert into ${AkariSchema.AkariEnv.table}(key, value, type) values ('db_version', '0', 'int')")
    }

    //We must fail the database if akari db does not init. This ensures further corruption will not occur.
    return akariOnCreateSql.requireOkOrThrow()
}