package com.ord.seeders.entities.bases

interface SeederInterface<TEntity> {
    /**
     * Insert a singular row into the database
     */
    fun seedOneEntity(data: TEntity? = null): TEntity {
        throw RuntimeException("Method not allowed")
    }

    /**
     * Delete all rows from the database
     */
    fun deleteAll() {
        throw RuntimeException("Method not allowed")
    }
}