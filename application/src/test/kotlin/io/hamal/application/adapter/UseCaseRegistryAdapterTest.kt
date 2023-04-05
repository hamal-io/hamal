package io.hamal.application.adapter

import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.meta.Maybe
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.NotFoundException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import java.util.*

@Nested
class UseCaseRegistryAdapterTest {

    @Nested
    @DisplayName("GetCommandUseCasePort")
    inner class GetCommandUseCasePortTestPayload {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestCommandUseCasePayload::class]
            assertThat(result, equalTo(testCommandUseCase))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestCommandUseCasePayload::class]
            }
            assertThat(exception.message, equalTo("CommandUseCase<TestResult,TestCommandUseCasePayload> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestCommandUseCasePayload::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestCommandUseCasePayload::class]
            assertThat(result, equalTo(testCommandUseCase))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestCommandUseCasePayload::class]
            assertThat(result, equalTo(testCommandUseCase))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestCommandUseCaseInterfacePayload::class,
                    testCommandInterfaceUseCase
                )
            }
            val result = testInstance[TestResult::class, TestCommandUseCaseInterfacePayload::class]
            assertThat(result, equalTo(testCommandInterfaceUseCase))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestCommandUseCasePayload::class, testCommandUseCase)
        }

        private val testCommandUseCase = TestCommandUseCase()
        private val testCommandInterfaceUseCase = TestCommandUseCaseInterfacePayload.Handler()

    }

    @Nested
    @DisplayName("GetQueryUseCasePort")
    inner class GetQueryUseCasePortTestPayload {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestQueryUseCasePayload::class]
            assertThat(result, equalTo(testQueryUseCase))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestQueryUseCasePayload::class]
            }
            assertThat(exception.message, equalTo("QueryUseCase<TestResult,TestQueryUseCasePayload> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestQueryUseCasePayload::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestQueryUseCasePayload::class]
            assertThat(result, equalTo(testQueryUseCase))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestQueryUseCasePayload::class]
            assertThat(result, equalTo(testQueryUseCase))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestQueryUseCaseInterfacePayload::class,
                    testQueryInterfaceUseCase
                )
            }
            val result = testInstance[TestResult::class, TestQueryUseCaseInterfacePayload::class]
            assertThat(result, equalTo(testQueryInterfaceUseCase))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestQueryUseCasePayload::class, testQueryUseCase)
        }

        private val testQueryUseCase = TestQueryUseCase()
        private val testQueryInterfaceUseCase = TestQueryUseCaseInterfacePayload.Handler()

    }

    @Nested
    @DisplayName("GetFetchOneUseCasePort")
    inner class GetFetchOneUseCasePortTestPayload {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestFetchOneUseCasePayload::class]
            assertThat(result, equalTo(testFetchOneUseCase))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestFetchOneUseCasePayload::class]
            }
            assertThat(exception.message, equalTo("FetchOneUseCase<TestResult,TestFetchOneUseCasePayload> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestFetchOneUseCasePayload::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestFetchOneUseCasePayload::class]
            assertThat(result, equalTo(testFetchOneUseCase))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestFetchOneUseCasePayload::class]
            assertThat(result, equalTo(testFetchOneUseCase))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestFetchOneUseCaseInterfacePayload::class,
                    testFetchOneInterfaceUseCase
                )
            }
            val result = testInstance[TestResult::class, TestFetchOneUseCaseInterfacePayload::class]
            assertThat(result, equalTo(testFetchOneInterfaceUseCase))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestFetchOneUseCasePayload::class, testFetchOneUseCase)
        }

        private val testFetchOneUseCase = TestFetchOneUseCase()
        private val testFetchOneInterfaceUseCase = TestFetchOneUseCaseInterfacePayload.Handler()

    }

    private interface TestResultInterface
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestCommandUseCasePayload : CommandUseCasePayload

    private class TestCommandUseCase :
        CommandUseCase.BaseImpl<TestResult, TestCommandUseCasePayload>(
            TestResult::class, TestCommandUseCasePayload()
        ) {
        override operator fun invoke(payload: TestCommandUseCasePayload): List<TestResult> {
            return listOf()
        }
    }

    private interface TestCommandUseCaseInterfacePayload : CommandUseCasePayload {
        class Handler : CommandUseCase.BaseImpl<TestResult, TestCommandUseCaseInterfacePayload>(
            TestResult::class, object : TestCommandUseCaseInterfacePayload {}
        ) {
            override operator fun invoke(payload: TestCommandUseCaseInterfacePayload): List<TestResult> {
                return listOf()
            }
        }
    }


    private class TestQueryUseCasePayload : QueryUseCasePayload
    private class TestQueryUseCase :
        QueryUseCase.BaseImpl<TestResult, TestQueryUseCasePayload>(
            TestResult::class, TestQueryUseCasePayload()
        ) {
        override operator fun invoke(payload: TestQueryUseCasePayload): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryUseCaseInterfacePayload : QueryUseCasePayload {
        class Handler : QueryUseCase.BaseImpl<TestResult, TestQueryUseCaseInterfacePayload>(
            TestResult::class, object : TestQueryUseCaseInterfacePayload {}
        ) {
            override operator fun invoke(payload: TestQueryUseCaseInterfacePayload): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestFetchOneUseCasePayload : FetchOneUseCasePayload
    private class TestFetchOneUseCase :
        FetchOneUseCase.BaseImpl<TestResult, TestFetchOneUseCasePayload>(
            TestResult::class, TestFetchOneUseCasePayload()
        ) {
        override operator fun invoke(payload: TestFetchOneUseCasePayload): Maybe<TestResult> {
            return Maybe.none()
        }
    }

    private interface TestFetchOneUseCaseInterfacePayload : FetchOneUseCasePayload {
        class Handler : FetchOneUseCase.BaseImpl<TestResult, TestFetchOneUseCaseInterfacePayload>(
            TestResult::class, object : TestFetchOneUseCaseInterfacePayload {}
        ) {
            override operator fun invoke(payload: TestFetchOneUseCaseInterfacePayload): Maybe<TestResult> {
                return Maybe.none()
            }
        }
    }
}