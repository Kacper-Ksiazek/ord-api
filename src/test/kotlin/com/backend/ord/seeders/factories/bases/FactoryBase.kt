package com.backend.ord.seeders.factories.bases

import com.github.javafaker.Faker

abstract class FactoryBase(
    protected val faker: Faker = Faker()
)