package io.hamal.backend.infra.adapter

import io.hamal.backend.infra.adapter.TestUseCasesConfig.*
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
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
 * Registers testRequestOneUseCase, testQueryManyUseCase, testQueryOneUseCase so that the use case registry can pick up
 * those beans
 */

@Configuration
open class TestUseCasesConfig {
    @Bean
    open fun requestOneUseCase() = testRequestOneUseCaseOp

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
    inner class GetRequestOneUseCasePortTestOperation {
        @Test
        fun `Registers requestOne use case operation`() {
            val result = testInstance[TestRequestOneUseCase::class]
            assertThat(result, equalTo(testRequestOneUseCaseOp))
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

class TestId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}


data class TestResult(override val id: TestId) : DomainObject<TestId>

class TestRequestOneUseCase : RequestOneUseCase<TestResult> {
    override val requestId = RequestId(123)
    override val shard = Shard(23)
}

private val testRequestOneUseCaseOp =
    object : RequestOneUseCaseHandler<TestResult, TestRequestOneUseCase>(TestRequestOneUseCase::class) {
        override operator fun invoke(useCase: TestRequestOneUseCase) = TestResult(TestId(42))
    }

class TestQueryManyUseCase : QueryManyUseCase<TestResult>

private val testQueryManyUseCaseOp =
    object : QueryManyUseCaseHandler<TestResult, TestQueryManyUseCase>(TestQueryManyUseCase::class) {
        override fun invoke(useCase: TestQueryManyUseCase) = listOf<TestResult>()
    }

class TestQueryOneUseCase : QueryOneUseCase<TestResult>

private val testQueryOneUseCaseOp =
    object : QueryOneUseCaseHandler<TestResult, TestQueryOneUseCase>(TestQueryOneUseCase::class) {
        override fun invoke(useCase: TestQueryOneUseCase) = TestResult(TestId(0))
    }
