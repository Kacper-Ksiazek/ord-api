package com.ord.seeders.entities.bases

interface SeederInterface<T> {
    /**
     * Insert a singular row into the database
     */
    fun seedOneEntity(data: T? = null): T {
        throw RuntimeException("Method not allowed")
    }

    /**
     * Delete all rows from the database
     */
    fun deleteAll()
}