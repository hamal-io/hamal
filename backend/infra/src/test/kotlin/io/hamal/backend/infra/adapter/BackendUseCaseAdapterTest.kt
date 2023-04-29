package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.NopLogger
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import java.util.*
import kotlin.math.pow

class BackendUseCaseRegistryAdapterTest {

    @Nested
    @DisplayName("GetExecuteOneUseCasePort")
    inner class GetExecuteOneUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestExecuteOneUseCase::class]
            assertThat(result, equalTo(testExecuteOneUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestExecuteOneUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestExecuteOneUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestExecuteOneUseCaseInterface::class,
                    testExecuteOneInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestExecuteOneUseCaseInterface::class]
            assertThat(result, equalTo(testExecuteOneInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestExecuteOneUseCase::class, testExecuteOneUseCaseOp)
        }

        private val testExecuteOneUseCaseOp = TestExecuteOneUseCaseOperation()
        private val testExecuteOneInterfaceUseCaseOp = TestExecuteOneUseCaseInterface.Operation()

    }

    @Nested
    @DisplayName("GetQueryManyUseCasePort")
    inner class GetQueryManyUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestQueryManyUseCase::class]
            assertThat(result, equalTo(testQueryManyUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestQueryManyUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestQueryManyUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestQueryManyUseCaseInterface::class,
                    testQueryManyInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestQueryManyUseCaseInterface::class]
            assertThat(result, equalTo(testQueryManyInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestQueryManyUseCase::class, testQueryManyUseCaseOp)
        }

        private val testQueryManyUseCaseOp = TestQueryManyUseCaseOperation()
        private val testQueryManyInterfaceUseCaseOp = TestQueryManyUseCaseInterface.Handler()

    }

    @Nested
    @DisplayName("GetQueryOneUseCasePort")
    inner class GetQueryOneUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestQueryOneUseCase::class]
            assertThat(result, equalTo(testQueryOneUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestQueryOneUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestQueryOneUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestQueryOneUseCaseInterface::class,
                    testQueryOneInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestQueryOneUseCaseInterface::class]
            assertThat(result, equalTo(testQueryOneInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestQueryOneUseCase::class, testQueryOneUseCaseOp)
        }

        private val testQueryOneUseCaseOp = TestQueryOneUseCaseOperation()
        private val testQueryOneInterfaceUseCaseOp = TestQueryOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface : DomainObject
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestExecuteOneUseCase : ExecuteOneUseCase<TestResult>

    private class TestExecuteOneUseCaseOperation :
        ExecuteOneUseCaseOperation<TestResult, TestExecuteOneUseCase>(TestExecuteOneUseCase::class) {
        override operator fun invoke(useCase: TestExecuteOneUseCase): TestResult {
            return TestResult()
        }
    }

    private interface TestExecuteOneUseCaseInterface : ExecuteOneUseCase<TestResult> {
        class Operation :
            ExecuteOneUseCaseOperation<TestResult, TestExecuteOneUseCaseInterface>(TestExecuteOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestExecuteOneUseCaseInterface): TestResult {
                return TestResult()
            }
        }
    }


    private class TestQueryManyUseCase : QueryManyUseCase<TestResult>
    private class TestQueryManyUseCaseOperation :
        QueryManyUseCaseOperation<TestResult, TestQueryManyUseCase>(TestQueryManyUseCase::class) {
        override operator fun invoke(useCase: TestQueryManyUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryManyUseCaseInterface : QueryManyUseCase<TestResult> {
        class Handler :
            QueryManyUseCaseOperation<TestResult, TestQueryManyUseCaseInterface>(TestQueryManyUseCaseInterface::class) {
            override operator fun invoke(useCase: TestQueryManyUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestQueryOneUseCase : QueryOneUseCase<TestResult>
    private class TestQueryOneUseCaseOperation :
        QueryOneUseCaseOperation<TestResult, TestQueryOneUseCase>(TestQueryOneUseCase::class) {
        override operator fun invoke(useCase: TestQueryOneUseCase): TestResult {
            return TestResult()
        }
    }

    private interface TestQueryOneUseCaseInterface : QueryOneUseCase<TestResult> {
        class Handler :
            QueryOneUseCaseOperation<TestResult, TestQueryOneUseCaseInterface>(TestQueryOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestQueryOneUseCaseInterface): TestResult {
                return TestResult()
            }
        }
    }
}

@Nested
@DisplayName("BackendUseCaseInvokerAdapter")
class BackendUseCaseInvokerAdapterTest {
    @Nested
    @DisplayName("invoke<ExecuteOne>()")
    inner class ExecuteOneTest {

        @Test
        fun `Applies operation on use case`() {
            val result = testInstance(TestUseCase(100))
            assertThat(result, equalTo(TestResult(200)))
        }

        private inner class TestUseCase(val data: Int) : ExecuteOneUseCase<TestResult>
        private inner class TestUseCaseOperation :
            ExecuteOneUseCaseOperation<TestResult, TestUseCase>(TestUseCase::class) {
            override fun invoke(useCase: TestUseCase): TestResult {
                return TestResult(useCase.data * 2)
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter, {}, { NopLogger })
    }

    @Nested
    @DisplayName("invoke<QueryMany>()")
    inner class QueryManyTest {
        @Test
        fun `Applies operation on use case`() {
            val result = testInstance.invoke(TestQueryManyUseCase(3))
            assertThat(result, equalTo(listOf(TestResult(10), TestResult(100), TestResult(1000))))
        }

        private inner class TestQueryManyUseCase(val data: Int) : QueryManyUseCase<TestResult>
        private inner class TestQueryManyUseCaseOperation : QueryManyUseCaseOperation<TestResult, TestQueryManyUseCase>(
            TestQueryManyUseCase::class
        ) {
            override fun invoke(useCase: TestQueryManyUseCase): List<TestResult> {
                return IntRange(1, useCase.data)
                    .map { TestResult(10.0.pow(it.toDouble()).toInt()) }
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestQueryManyUseCase::class, TestQueryManyUseCaseOperation())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter, {}, { NopLogger })
    }

    @Nested
    @DisplayName("invoke<QueryOne>()")
    inner class QueryOneTest {
        @Test
        fun `Applies operation on use case`() {
            val result = testInstance(TestUseCase(3))
            assertThat(result, equalTo(TestResult(2810)))
        }

        private inner class TestUseCase(val data: Int) : QueryOneUseCase<TestResult>
        private inner class TestUseCaseOperation : QueryOneUseCaseOperation<TestResult, TestUseCase>(
            TestUseCase::class
        ) {
            override fun invoke(useCase: TestUseCase): TestResult {
                return TestResult(2810)
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter, {}, { NopLogger })
    }

    private inner class TestResult(val data: Int) : DomainObject {
        override fun equals(other: Any?) = data == (other as TestResult).data
        override fun hashCode() = data
    }

}

