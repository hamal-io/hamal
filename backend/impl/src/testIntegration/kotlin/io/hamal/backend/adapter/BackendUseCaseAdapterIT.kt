package io.hamal.backend.adapter

import io.hamal.backend.BackendUseCaseRegistryAdapter
import io.hamal.backend.adapter.TestUseCasesConfig.*
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.*
import io.hamal.lib.domain.vo.base.DomainId
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
    open fun requestOneUseCase() = testRequestOneUseCaseHandler

    @Bean
    open fun requestManyUseCase() = testRequestManyUseCaseHandler

    @Bean
    open fun queryManyUseCase() = testQueryManyUseCaseHandler

    @Bean
    open fun queryOneUseCase() = testQueryOneUseCaseHandler
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
        fun `Registers request one use case handler`() {
            val result = testInstance[TestRequestOneUseCase::class]
            assertThat(result, equalTo(testRequestOneUseCaseHandler))
        }

        @Test
        fun `Registers request many use case handler`() {
            val result = testInstance[TestRequestManyUseCase::class]
            assertThat(result, equalTo(testRequestManyUseCaseHandler))
        }


        @Test
        fun `Registers query many use case handler`() {
            val result = testInstance[TestQueryManyUseCase::class]
            assertThat(result, equalTo(testQueryManyUseCaseHandler))
        }

        @Test
        fun `Registers query one use case handler`() {
            val result = testInstance[TestQueryOneUseCase::class]
            assertThat(result, equalTo(testQueryOneUseCaseHandler))
        }
    }
}

class TestId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}


data class TestResult(override val id: TestId) : DomainObject<TestId>

class TestRequestOneUseCase : RequestOneUseCase<TestResult> {
    override val reqId = ReqId(123)
    override val shard = Shard(23)
}

private val testRequestOneUseCaseHandler =
    object : RequestOneUseCaseHandler<TestResult, TestRequestOneUseCase>(TestRequestOneUseCase::class) {
        override operator fun invoke(useCase: TestRequestOneUseCase) = TestResult(TestId(42))
    }

class TestRequestManyUseCase : RequestManyUseCase<TestResult> {
    override val reqId = ReqId(123)
    override val shard = Shard(23)
}

private val testRequestManyUseCaseHandler =
    object : RequestManyUseCaseHandler<TestResult, TestRequestManyUseCase>(TestRequestManyUseCase::class) {
        override operator fun invoke(useCase: TestRequestManyUseCase) = listOf(TestResult(TestId(4242)))
    }

class TestQueryManyUseCase : QueryManyUseCase<TestResult>

private val testQueryManyUseCaseHandler =
    object : QueryManyUseCaseHandler<TestResult, TestQueryManyUseCase>(TestQueryManyUseCase::class) {
        override fun invoke(useCase: TestQueryManyUseCase) = listOf<TestResult>()
    }

class TestQueryOneUseCase : QueryOneUseCase<TestResult>

private val testQueryOneUseCaseHandler =
    object : QueryOneUseCaseHandler<TestResult, TestQueryOneUseCase>(TestQueryOneUseCase::class) {
        override fun invoke(useCase: TestQueryOneUseCase) = TestResult(TestId(0))
    }
