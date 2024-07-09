package com.backend.ord.seeders.entities

interface SeederInterface<T> {
    /**
     * Insert a singular row into the database
     */
    fun insertRow(): T

    fun deleteAll()
}
