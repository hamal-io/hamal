package io.hamal.application.adapter

import io.hamal.application.adapter.TestUseCasesConfig.*
import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.meta.Maybe
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
    open fun commandUseCase() = testCommandUseCase

    @Bean
    open fun queryUseCase() = testQueryUseCase

    @Bean
    open fun fetchOneUseCase() = testFetchOneUseCase
}

@Nested
@SpringBootTest(
    classes = [TestApplication::class, TestUseCaseRegistryConfig::class, TestUseCasesConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UseCasePayloadRegistryAdapterIT(
    @Autowired var testInstance: DefaultUseCaseRegistryAdapter
) {

    @Nested
    @DisplayName("onApplicationEvent()")
    inner class GetCommandUseCasePortTest {

        @Test
        fun `Registers command use case`() {
            val result = testInstance[String::class, TestCommandUseCasePayload::class]
            assertThat(result, equalTo(testCommandUseCase))
        }

        @Test
        fun `Registers query use case`() {
            val result = testInstance[String::class, TestQueryUseCasePayload::class]
            assertThat(result, equalTo(testQueryUseCase))
        }

        @Test
        fun `Registers fetchone use case`() {
            val result = testInstance[String::class, TestFetchOneUseCasePayload::class]
            assertThat(result, equalTo(testFetchOneUseCase))
        }
    }
}

class TestCommandUseCasePayload : CommandUseCasePayload

private val testCommandUseCase = object : CommandUseCase<String, TestCommandUseCasePayload>(
    String::class,
    TestCommandUseCasePayload::class
) {
    override operator fun invoke(payload: TestCommandUseCasePayload) = emptyList<String>()
}

class TestQueryUseCasePayload : QueryUseCasePayload

private val testQueryUseCase = object : QueryUseCase<String, TestQueryUseCasePayload>(
    String::class,
    TestQueryUseCasePayload::class
) {
    override fun invoke(payload: TestQueryUseCasePayload) = listOf<String>()
}

class TestFetchOneUseCasePayload : FetchOneUseCasePayload

private val testFetchOneUseCase = object : FetchOneUseCase<String, TestFetchOneUseCasePayload>(
    String::class,
    TestFetchOneUseCasePayload::class
) {
    override fun invoke(payload: TestFetchOneUseCasePayload) = Maybe.none<String>()
}
