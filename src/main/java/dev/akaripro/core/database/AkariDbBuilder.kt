package dev.akaripro.core.database

private val defaultCallbacks = object: DatabaseCallbacks {
    override fun onCreate(db: AkariDb) {}
    override fun onUpgrade(db: AkariDb, toVersion: Int) {}
}

class AkariDbBuilder {
    var name: String? = null
    var callbacks: DatabaseCallbacks = defaultCallbacks
    var type: DatabaseType? = null
}