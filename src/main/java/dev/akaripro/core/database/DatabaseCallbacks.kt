package dev.akaripro.core.database

interface DatabaseCallbacks {
    fun onCreate(db: AkariDb)
    fun onUpgrade(db: AkariDb, toVersion: Int)
}