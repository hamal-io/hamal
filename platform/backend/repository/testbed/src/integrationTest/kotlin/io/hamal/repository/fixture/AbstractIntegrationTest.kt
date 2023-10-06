package io.hamal.repository.fixture

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import kotlin.reflect.KClass

abstract class AbstractIntegrationTest {

    fun <REPO : AutoCloseable> runWith(
        interfaceClass: KClass<out REPO>,
        block: (testInstance: REPO) -> Unit
    ): List<DynamicTest> {
        return provideTestInstances(interfaceClass).map { testInstance ->
            dynamicTest(testInstance::class.simpleName) {
                testInstance.use(block)
            }
        }
    }

    private fun <REPO : Any> provideTestInstances(interfaceClass: KClass<out REPO>): List<REPO> {
        return fixtures[interfaceClass]!!
            .map { fixture -> fixture.provideImplementation(interfaceClass) }
    }

    private val fixtures = mutableMapOf<KClass<*>, List<BaseTestFixture>>(
        BrokerRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        BrokerTopicsRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        )
    )
}