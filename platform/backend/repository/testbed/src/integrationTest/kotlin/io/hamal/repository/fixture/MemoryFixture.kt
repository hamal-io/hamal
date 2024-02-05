package io.hamal.repository.fixture

import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.memory.new_log.LogBrokerMemoryRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        LogBrokerRepository::class -> LogBrokerMemoryRepository() as REPO
        else -> TODO()
    }
}