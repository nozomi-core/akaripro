package dev.akaripro.core.database

import java.io.File

sealed class DatabaseType {
    object InMemory : DatabaseType()
    class FileDatabase(val parentDir: File): DatabaseType()
}