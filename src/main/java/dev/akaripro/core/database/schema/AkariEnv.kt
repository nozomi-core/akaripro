package dev.akaripro.core.database.schema

object AkariEnv {
    const val table = "akari_env"

    const val key = "key"
    const val value = "value"
    const val type = "type"

    object Keys {
        const val dbVersion = "db_version"
    }

    object Types {
        const val integer = "int"
    }

    const val create = """
        create table $table(
            $key TEXT PRIMARY KEY ON CONFLICT REPLACE,
            $value TEXT,
            $type TEXT
        );
     """

    object Queries {
        const val selectDbVersion = "select * from $table where $key= '${Keys.dbVersion}'"

        fun insertDbVersion(version: Int): String =
            "insert into $table($key, $value, $type) values ('${Keys.dbVersion}', '${version}', '${Types.integer}')"
    }
}