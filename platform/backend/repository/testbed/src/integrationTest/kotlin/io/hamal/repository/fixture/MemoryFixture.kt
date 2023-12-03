package io.hamal.repository.fixture

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.memory.log.BrokerMemoryRepository
import io.hamal.repository.memory.log.BrokerTopicsMemoryRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerRepository::class -> BrokerMemoryRepository() as REPO
        BrokerTopicsRepository::class -> BrokerTopicsMemoryRepository() as REPO
        else -> TODO()
    }
}