package io.hamal.repository.fixture

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.memory.log.MemoryBrokerRepository
import io.hamal.repository.memory.log.MemoryBrokerTopicsRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerRepository::class -> MemoryBrokerRepository() as REPO
        BrokerTopicsRepository::class -> MemoryBrokerTopicsRepository() as REPO
        else -> TODO()
    }
}