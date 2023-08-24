package io.hamal.faas.instance.backend.repository

import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.faas.instance.backend.repository.fixture.BaseTestFixture
import io.hamal.faas.instance.backend.repository.fixture.MemoryFixture
import io.hamal.faas.instance.backend.repository.fixture.SqliteFixture
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import kotlin.reflect.KClass

abstract class AbstractTest {

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
        LogBrokerRepository::class to listOf(
            MemoryFixture,
            SqliteFixture
        ),
        FuncQueryRepository::class to listOf(

        ),
        FuncCmdRepository::class to listOf(

        )
    )
}