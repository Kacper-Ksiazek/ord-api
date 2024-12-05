package com.backend.ord.seeders.entities

interface SeederInterface<T> {
    /**
     * Insert a singular row into the database
     */
    fun seedOneEntity(data: T? = null): T

    /**
     * Delete all rows from the database
     */
    fun deleteAll()
}
