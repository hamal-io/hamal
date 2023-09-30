package io.hamal.repository.fixture

import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import kotlin.reflect.KClass

abstract class AbstractUnitTest {

    fun <REPO : CmdRepository> runWith(
        interfaceClass: KClass<out REPO>,
        description: String? = null,
        block: REPO.() -> Unit
    ): List<DynamicTest> {
        return provideTestInstances(interfaceClass).map { testInstance ->
            testInstance.clear()
            dynamicTest("${testInstance::class.simpleName} ${description ?: ""}") {
                testInstance.use(block)
            }
        }
    }

    private fun <REPO : Any> provideTestInstances(interfaceClass: KClass<out REPO>): List<REPO> {
        return fixtures[interfaceClass]!!
            .map { fixture -> fixture.provideImplementation(interfaceClass) }
    }

    private val fixtures = mutableMapOf<KClass<*>, List<BaseTestFixture>>(
        AccountRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        AuthRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
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
        ExecLogRepository::class to listOf(
            MemoryFixture
//FIXME     SqliteFixture
        ),
        ExecRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        FuncRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        GroupRepository::class to listOf(
            MemoryFixture,
// FIXME    SqliteFixture
        ),
        NamespaceRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        ReqRepository::class to listOf(
            MemoryFixture,
// FIXME    SqliteFixture
        ),
        StateRepository::class to listOf(
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
        ),
        TriggerRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        )
    )
}