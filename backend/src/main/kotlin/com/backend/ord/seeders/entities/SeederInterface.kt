package com.backend.ord.seeders.entities

interface SeederInterface<T> {
    /**
     * Insert a singular row into the database
     */
    fun seedOneEntity(data: T? = null): T

    fun deleteAll()
}
