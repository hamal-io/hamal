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

            val result = testInstance[TestResult::class.java, TestCommandUseCase::class.java]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class.java, TestCommandUseCase::class.java]
            }
            assertThat(exception.message, equalTo("CommandUseCaseHandler<TestResult,TestCommandUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class.java, TestCommandUseCase::class.java]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class.java, TestCommandUseCase::class.java]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class.java, TestCommandUseCase::class.java]
            assertThat(result, equalTo(testCommandUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestCommandUseCaseInterface::class.java,
                    testCommandInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class.java, TestCommandUseCaseInterface::class.java]
            assertThat(result, equalTo(testCommandInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestCommandUseCase::class.java, testCommandUseCaseHandler)
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

            val result = testInstance[TestResult::class.java, TestQueryUseCase::class.java]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class.java, TestQueryUseCase::class.java]
            }
            assertThat(exception.message, equalTo("QueryUseCaseHandler<TestResult,TestQueryUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class.java, TestQueryUseCase::class.java]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class.java, TestQueryUseCase::class.java]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class.java, TestQueryUseCase::class.java]
            assertThat(result, equalTo(testQueryUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestQueryUseCaseInterface::class.java,
                    testQueryInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class.java, TestQueryUseCaseInterface::class.java]
            assertThat(result, equalTo(testQueryInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestQueryUseCase::class.java, testQueryUseCaseHandler)
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

            val result = testInstance[TestResult::class.java, TestFetchOneUseCase::class.java]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<NotFoundException> {
                testInstance[TestResult::class.java, TestFetchOneUseCase::class.java]
            }
            assertThat(exception.message, equalTo("FetchOneUseCaseHandler<TestResult,TestFetchOneUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class.java, TestFetchOneUseCase::class.java]
            }
            assertThat(
                exception.message,
                equalTo("result class(IncompatibleTestResult) does not match with UseCase result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class.java, TestFetchOneUseCase::class.java]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class.java, TestFetchOneUseCase::class.java]
            assertThat(result, equalTo(testFetchOneUseCaseHandler))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestFetchOneUseCaseInterface::class.java,
                    testFetchOneInterfaceUseCaseHandler
                )
            }
            val result = testInstance[TestResult::class.java, TestFetchOneUseCaseInterface::class.java]
            assertThat(result, equalTo(testFetchOneInterfaceUseCaseHandler))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestFetchOneUseCase::class.java, testFetchOneUseCaseHandler)
        }

        private val testFetchOneUseCaseHandler = TestFetchOneUseCaseHandler()
        private val testFetchOneInterfaceUseCaseHandler = TestFetchOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestCommandUseCase : CommandUseCase


    private class TestCommandUseCaseHandler :
        CommandUseCaseHandler.BaseImpl<TestResult, TestCommandUseCase>() {
        override fun handle(useCase: TestCommandUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestCommandUseCaseInterface : CommandUseCase {
        class Handler : CommandUseCaseHandler.BaseImpl<TestResult, TestCommandUseCaseInterface>() {
            override fun handle(useCase: TestCommandUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }


    private class TestQueryUseCase : QueryUseCase
    private class TestQueryUseCaseHandler :
        QueryUseCaseHandler.BaseImpl<TestResult, TestQueryUseCase>() {
        override fun handle(useCase: TestQueryUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryUseCaseInterface : QueryUseCase {
        class Handler : QueryUseCaseHandler.BaseImpl<TestResult, TestQueryUseCaseInterface>() {
            override fun handle(useCase: TestQueryUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestFetchOneUseCase : FetchOneUseCase
    private class TestFetchOneUseCaseHandler :
        FetchOneUseCaseHandler.BaseImpl<TestResult, TestFetchOneUseCase>() {
        override fun handle(useCase: TestFetchOneUseCase): Maybe<TestResult> {
            return Maybe.none()
        }
    }

    private interface TestFetchOneUseCaseInterface : FetchOneUseCase {
        class Handler : FetchOneUseCaseHandler.BaseImpl<TestResult, TestFetchOneUseCaseInterface>() {
            override fun handle(useCase: TestFetchOneUseCaseInterface): Maybe<TestResult> {
                return Maybe.none()
            }
        }
    }
}