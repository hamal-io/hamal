package io.hamal.repository.fixture

import io.hamal.repository.api.FuncRepository
import io.hamal.repository.api.Repository
import io.hamal.repository.api.log.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import kotlin.reflect.KClass

abstract class AbstractUnitTest {

    fun <REPO : Repository> runWith(
        interfaceClass: KClass<out REPO>,
        block: REPO.() -> Unit
    ): List<DynamicTest> {
        return provideTestInstances(interfaceClass).map { testInstance ->
            testInstance.clear()
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
        BrokerConsumersRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        BrokerRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        BrokerTopicsRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        FuncRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        SegmentRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        TopicRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        )
    )
}