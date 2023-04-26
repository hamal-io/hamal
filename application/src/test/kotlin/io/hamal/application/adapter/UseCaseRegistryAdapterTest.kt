package io.hamal.application.adapter

import io.hamal.lib.ddd.usecase.*
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import java.util.*

@Nested
class UseCaseRegistryAdapterTest {

    @Nested
    @DisplayName("GetCommandUseCasePort")
    inner class GetCommandUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestResult::class, TestCommandUseCase::class]
            }
            assertThat(exception.message, containsString("CommandUseCaseOperation<TestResult,TestCommandUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestCommandUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseOp))
        }

        @Test
        fun `Ignore unit as result class`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[Unit::class, TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseOp))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestCommandUseCaseInterface::class,
                    testCommandInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestResult::class, TestCommandUseCaseInterface::class]
            assertThat(result, equalTo(testCommandInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestCommandUseCase::class, testCommandUseCaseOp)
        }

        private val testCommandUseCaseOp = TestCommandUseCaseOperation()
        private val testCommandInterfaceUseCaseOp = TestCommandUseCaseInterface.Handler()

    }

    @Nested
    @DisplayName("GetQueryUseCasePort")
    inner class GetQueryUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestResult::class, TestQueryUseCase::class]
            }
            assertThat(exception.message, containsString("QueryUseCaseOperation<TestResult,TestQueryUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestQueryUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseOp))
        }

        @Test
        fun `Throws exception if result class is Unit`() {
            val testInstance = testInstanceWithUseCase()
            val exception = assertThrows<IllegalArgumentException> {
                testInstance[Unit::class, TestQueryUseCase::class]
            }
            assertThat(exception.message, containsString("Result class can not be Unit"))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestQueryUseCaseInterface::class,
                    testQueryInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestResult::class, TestQueryUseCaseInterface::class]
            assertThat(result, equalTo(testQueryInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestQueryUseCase::class, testQueryUseCaseOp)
        }

        private val testQueryUseCaseOp = TestQueryUseCaseOperation()
        private val testQueryInterfaceUseCaseOp = TestQueryUseCaseInterface.Handler()

    }

    @Nested
    @DisplayName("GetFetchOneUseCasePort")
    inner class GetFetchOneUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestResult::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = DefaultUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestResult::class, TestFetchOneUseCase::class]
            }
            assertThat(exception.message, containsString("FetchOneUseCaseOperation<TestResult,TestFetchOneUseCase> not found"))
        }

        @Test
        fun `Result class is not compatible`() {
            val testInstance = testInstanceWithUseCase()

            val exception = assertThrows<IllegalArgumentException> {
                testInstance[IncompatibleTestResult::class, TestFetchOneUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("result class(IncompatibleTestResult) does not match with use case result class(TestResult)")
            )
        }

        @Test
        fun `Result class is assignable to interface`() {
            val testInstance = testInstanceWithUseCase()
            val result = testInstance[TestResultInterface::class, TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseOp))
        }

        @Test
        fun `Throws exception if result class is Unit`() {
            val testInstance = testInstanceWithUseCase()
            val exception = assertThrows<IllegalArgumentException> {
                testInstance[Unit::class, TestFetchOneUseCase::class]
            }
            assertThat(exception.message, containsString("Result class can not be Unit"))
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = DefaultUseCaseRegistryAdapter().apply {
                register(
                    TestFetchOneUseCaseInterface::class,
                    testFetchOneInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestResult::class, TestFetchOneUseCaseInterface::class]
            assertThat(result, equalTo(testFetchOneInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = DefaultUseCaseRegistryAdapter().apply {
            register(TestFetchOneUseCase::class, testFetchOneUseCaseOp)
        }

        private val testFetchOneUseCaseOp = TestFetchOneUseCaseOperation()
        private val testFetchOneInterfaceUseCaseOp = TestFetchOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestCommandUseCase : CommandUseCase

    private class TestCommandUseCaseOperation :
        CommandUseCaseOperation<TestResult, TestCommandUseCase>(
            TestResult::class, TestCommandUseCase::class
        ) {
        override operator fun invoke(useCase: TestCommandUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestCommandUseCaseInterface : CommandUseCase {
        class Handler : CommandUseCaseOperation<TestResult, TestCommandUseCaseInterface>(
            TestResult::class, TestCommandUseCaseInterface::class
        ) {
            override operator fun invoke(useCase: TestCommandUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }


    private class TestQueryUseCase : QueryUseCase
    private class TestQueryUseCaseOperation :
        QueryUseCaseOperation<TestResult, TestQueryUseCase>(
            TestResult::class, TestQueryUseCase::class
        ) {
        override operator fun invoke(useCase: TestQueryUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryUseCaseInterface : QueryUseCase {
        class Handler : QueryUseCaseOperation<TestResult, TestQueryUseCaseInterface>(
            TestResult::class, TestQueryUseCaseInterface::class
        ) {
            override operator fun invoke(useCase: TestQueryUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestFetchOneUseCase : FetchOneUseCase
    private class TestFetchOneUseCaseOperation :
        FetchOneUseCaseOperation<TestResult, TestFetchOneUseCase>(
            TestResult::class, TestFetchOneUseCase::class
        ) {
        override operator fun invoke(useCase: TestFetchOneUseCase): TestResult? {
            return null
        }
    }

    private interface TestFetchOneUseCaseInterface : FetchOneUseCase {
        class Handler : FetchOneUseCaseOperation<TestResult, TestFetchOneUseCaseInterface>(
            TestResult::class, TestFetchOneUseCaseInterface::class
        ) {
            override operator fun invoke(useCase: TestFetchOneUseCaseInterface): TestResult? {
                return null
            }
        }
    }
}