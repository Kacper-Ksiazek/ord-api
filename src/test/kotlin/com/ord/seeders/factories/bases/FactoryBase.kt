package com.ord.seeders.factories.bases

import net.datafaker.Faker

abstract class FactoryBase(
    protected val faker: Faker = Faker()
)