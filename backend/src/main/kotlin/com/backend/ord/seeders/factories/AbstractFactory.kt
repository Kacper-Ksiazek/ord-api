package com.backend.ord.seeders.factories

import com.github.javafaker.Faker

abstract class AbstractFactory {
    companion object {
        protected val faker: Faker = Faker()
    }
}
