package dev.akaripro.core.database

object AkariSchema {

    object AkariEnv {
        const val table = "akari_env"

        const val key = "key"
        const val value =  "value"
        const val type = "type"

        object Keys {
            const val DB_VERSION = "db_version"
        }
    }
}