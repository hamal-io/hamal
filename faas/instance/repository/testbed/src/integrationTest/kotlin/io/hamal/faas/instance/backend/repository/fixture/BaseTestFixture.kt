package io.hamal.faas.instance.backend.repository.fixture

import kotlin.reflect.KClass

interface BaseTestFixture {
    fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO
}