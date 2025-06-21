package com.ord.seeders.factories.bases

import com.github.javafaker.Faker

abstract class FactoryBase(
    protected val faker: Faker = Faker()
)