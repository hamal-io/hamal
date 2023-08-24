package io.hamal.faas.instance.backend.repository.fixture

import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.memory.log.MemoryBrokerTopicsRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerTopicsRepository::class -> MemoryBrokerTopicsRepository() as REPO
        else -> TODO()
    }
}