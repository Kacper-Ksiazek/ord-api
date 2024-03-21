package com.backend.ord.seeders.entities;

public interface SeederInterface <T>{
    /**
     * Insert a singular row into the database
     */
    T insertRow();

    void deleteAll();
}
