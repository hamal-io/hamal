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
    @DisplayName("GetCommandUseCaseHandlerPort")
    inner class GetCommandUseCaseHandlerPortTest {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestCommandUseCase::class]
            }
            assertThat(exception.message, equalTo("CommandUseCaseHandler<TestResult,TestCommandUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestCommandUseCase::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestCommandUseCaseInterface::class,
                    testCommandInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class, TestCommandUseCaseInterface::class]
            assertThat(result, equalTo(testCommandInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestCommandUseCase::class, testCommandUseCaseHandler)
        }

        private val testCommandUseCaseHandler = TestCommandUseCaseHandler()
        private val testCommandInterfaceUseCaseHandler = TestCommandUseCaseInterface.Handler()

    }

    @Nested
    @DisplayName("GetQueryUseCaseHandlerPort")
    inner class GetQueryUseCaseHandlerPortTest {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestQueryUseCase::class]
            }
            assertThat(exception.message, equalTo("QueryUseCaseHandler<TestResult,TestQueryUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestQueryUseCase::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestQueryUseCaseInterface::class,
                    testQueryInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class, TestQueryUseCaseInterface::class]
            assertThat(result, equalTo(testQueryInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestQueryUseCase::class, testQueryUseCaseHandler)
        }

        private val testQueryUseCaseHandler = TestQueryUseCaseHandler()
        private val testQueryInterfaceUseCaseHandler = TestQueryUseCaseInterface.Handler()

    }

    @Nested
    @DisplayName("GetFetchOneUseCaseHandlerPort")
    inner class GetFetchOneUseCaseHandlerPortTest {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class, TestFetchOneUseCase::class]
            }
            assertThat(exception.message, equalTo("FetchOneUseCaseHandler<TestResult,TestFetchOneUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestFetchOneUseCase::class]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestFetchOneUseCaseInterface::class,
                    testFetchOneInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class, TestFetchOneUseCaseInterface::class]
            assertThat(result, equalTo(testFetchOneInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestFetchOneUseCase::class, testFetchOneUseCaseHandler)
        }

        private val testFetchOneUseCaseHandler = TestFetchOneUseCaseHandler()
        private val testFetchOneInterfaceUseCaseHandler = TestFetchOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestCommandUseCase : CommandUseCase


    private class TestCommandUseCaseHandler :
        CommandUseCaseHandler.BaseImpl<TestResult, TestCommandUseCase>(
            TestResult::class, TestCommandUseCase::class
        ) {
        override fun handle(useCase: TestCommandUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestCommandUseCaseInterface : CommandUseCase {
        class Handler : CommandUseCaseHandler.BaseImpl<TestResult, TestCommandUseCaseInterface>(
            TestResult::class, TestCommandUseCaseInterface::class
        ) {
            override fun handle(useCase: TestCommandUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }


    private class TestQueryUseCase : QueryUseCase
    private class TestQueryUseCaseHandler :
        QueryUseCaseHandler.BaseImpl<TestResult, TestQueryUseCase>(
            TestResult::class, TestQueryUseCase::class
        ) {
        override fun handle(useCase: TestQueryUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryUseCaseInterface : QueryUseCase {
        class Handler : QueryUseCaseHandler.BaseImpl<TestResult, TestQueryUseCaseInterface>(
            TestResult::class, TestQueryUseCaseInterface::class
        ) {
            override fun handle(useCase: TestQueryUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestFetchOneUseCase : FetchOneUseCase
    private class TestFetchOneUseCaseHandler :
        FetchOneUseCaseHandler.BaseImpl<TestResult, TestFetchOneUseCase>(
            TestResult::class, TestFetchOneUseCase::class
        ) {
        override fun handle(useCase: TestFetchOneUseCase): Maybe<TestResult> {
            return Maybe.none()
        }
    }

    private interface TestFetchOneUseCaseInterface : FetchOneUseCase {
        class Handler : FetchOneUseCaseHandler.BaseImpl<TestResult, TestFetchOneUseCaseInterface>(
            TestResult::class, TestFetchOneUseCaseInterface::class
        ) {
            override fun handle(useCase: TestFetchOneUseCaseInterface): Maybe<TestResult> {
                return Maybe.none()
            }
        }
    }
}