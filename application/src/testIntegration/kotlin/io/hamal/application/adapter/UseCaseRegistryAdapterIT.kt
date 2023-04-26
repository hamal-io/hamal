package io.hamal.application.adapter

import io.hamal.application.adapter.TestUseCasesConfig.*
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
    open fun getDefaultUseCaseRegistryAdapter() = DefaultUseCaseRegistryAdapter()
}

/**
 * Registers testCommandUseCase, testQueryUseCase, testFetchOneUseCase so that the use case registry can pick up
 * those beans
 */

@Configuration
open class TestUseCasesConfig {
    @Bean
    open fun commandUseCase() = testCommandUseCaseOp

    @Bean
    open fun queryUseCase() = testQueryUseCaseOp

    @Bean
    open fun fetchOneUseCase() = testFetchOneUseCaseOp
}

@Nested
@SpringBootTest(
    classes = [TestApplication::class, TestUseCaseRegistryConfig::class, TestUseCasesConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UseCaseOperationPayloadRegistryAdapterIT(
    @Autowired var testInstance: DefaultUseCaseRegistryAdapter
) {

    @Nested
    @DisplayName("onApplicationEvent()")
    inner class GetCommandUseCasePortTestOperation {

        @Test
        fun `Registers command use case operation`() {
            val result = testInstance[String::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseOp))
        }

        @Test
        fun `Registers query use case operation`() {
            val result = testInstance[String::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseOp))
        }

        @Test
        fun `Registers fetchone use case operation`() {
            val result = testInstance[String::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseOp))
        }
    }
}

class TestCommandUseCase : CommandUseCase

private val testCommandUseCaseOp = object : CommandUseCaseOperation<String, TestCommandUseCase>(
    String::class,
    TestCommandUseCase::class
) {
    override operator fun invoke(useCase: TestCommandUseCase) = emptyList<String>()
}

class TestQueryUseCase : QueryUseCase

private val testQueryUseCaseOp = object : QueryUseCaseOperation<String, TestQueryUseCase>(
    String::class,
    TestQueryUseCase::class
) {
    override fun invoke(useCase: TestQueryUseCase) = listOf<String>()
}

class TestFetchOneUseCase : FetchOneUseCase

private val testFetchOneUseCaseOp = object : FetchOneUseCaseOperation<String, TestFetchOneUseCase>(
    String::class,
    TestFetchOneUseCase::class
) {
    override fun invoke(useCase: TestFetchOneUseCase) = null
}
