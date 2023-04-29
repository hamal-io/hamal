package io.hamal.backend.infra.adapter

import io.hamal.backend.infra.adapter.TestUseCasesConfig.*
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootTest
class TestApplication

@Configuration
open class TestUseCaseRegistryConfig {
    @Bean
    open fun getDefaultUseCaseRegistryAdapter() = BackendUseCaseRegistryAdapter()
}

/**
 * Registers testExecuteOneUseCase, testQueryManyUseCase, testQueryOneUseCase so that the use case registry can pick up
 * those beans
 */

@Configuration
open class TestUseCasesConfig {
    @Bean
    open fun executeOneUseCase() = testExecuteOneUseCaseOp

    @Bean
    open fun queryManyUseCase() = testQueryManyUseCaseOp

    @Bean
    open fun queryOneUseCase() = testQueryOneUseCaseOp
}

@Nested
@SpringBootTest(
    classes = [TestApplication::class, TestUseCaseRegistryConfig::class, TestUseCasesConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class BackendUseCaseRegistryAdapterIT(
    @Autowired var testInstance: BackendUseCaseRegistryAdapter
) {

    @Nested
    @DisplayName("onApplicationEvent()")
    inner class GetExecuteOneUseCasePortTestOperation {
        @Test
        fun `Registers executeOne use case operation`() {
            val result = testInstance[TestExecuteOneUseCase::class]
            assertThat(result, equalTo(testExecuteOneUseCaseOp))
        }

        @Test
        fun `Registers queryMany use case operation`() {
            val result = testInstance[TestQueryManyUseCase::class]
            assertThat(result, equalTo(testQueryManyUseCaseOp))
        }

        @Test
        fun `Registers queryOne use case operation`() {
            val result = testInstance[TestQueryOneUseCase::class]
            assertThat(result, equalTo(testQueryOneUseCaseOp))
        }
    }
}

data class TestResult(val value: Int) : DomainObject

class TestExecuteOneUseCase : ExecuteOneUseCase<TestResult>

private val testExecuteOneUseCaseOp =
    object : ExecuteOneUseCaseOperation<TestResult, TestExecuteOneUseCase>(TestExecuteOneUseCase::class) {
        override operator fun invoke(useCase: TestExecuteOneUseCase) = TestResult(42)
    }

class TestQueryManyUseCase : QueryManyUseCase<TestResult>

private val testQueryManyUseCaseOp =
    object : QueryManyUseCaseOperation<TestResult, TestQueryManyUseCase>(TestQueryManyUseCase::class) {
        override fun invoke(useCase: TestQueryManyUseCase) = listOf<TestResult>()
    }

class TestQueryOneUseCase : QueryOneUseCase<TestResult>

private val testQueryOneUseCaseOp =
    object : QueryOneUseCaseOperation<TestResult, TestQueryOneUseCase>(TestQueryOneUseCase::class) {
        override fun invoke(useCase: TestQueryOneUseCase) = null
    }
