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
    @DisplayName("GetRequestOneUseCasePort")
    inner class GetRequestOneUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestRequestOneUseCase::class]
            assertThat(result, equalTo(testRequestOneUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestRequestOneUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestRequestOneUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestRequestOneUseCaseInterface::class,
                    testRequestOneInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestRequestOneUseCaseInterface::class]
            assertThat(result, equalTo(testRequestOneInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestRequestOneUseCase::class, testRequestOneUseCaseOp)
        }

        private val testRequestOneUseCaseOp = TestRequestOneUseCaseHandler()
        private val testRequestOneInterfaceUseCaseOp = TestRequestOneUseCaseInterface.Operation()

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

        private val testQueryManyUseCaseOp = TestQueryManyUseCaseHandler()
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

        private val testQueryOneUseCaseOp = TestQueryOneUseCaseHandler()
        private val testQueryOneInterfaceUseCaseOp = TestQueryOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface : DomainObject
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestRequestOneUseCase : RequestOneUseCase<TestResult>{
        override val requestId= RequestId(123)
    }

    private class TestRequestOneUseCaseHandler :
        RequestOneUseCaseHandler<TestResult, TestRequestOneUseCase>(TestRequestOneUseCase::class) {
        override operator fun invoke(useCase: TestRequestOneUseCase): TestResult {
            return TestResult()
        }
    }

    private interface TestRequestOneUseCaseInterface : RequestOneUseCase<TestResult> {
        class Operation :
            RequestOneUseCaseHandler<TestResult, TestRequestOneUseCaseInterface>(TestRequestOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestRequestOneUseCaseInterface): TestResult {
                return TestResult()
            }
        }
    }


    private class TestQueryManyUseCase : QueryManyUseCase<TestResult>
    private class TestQueryManyUseCaseHandler :
        QueryManyUseCaseHandler<TestResult, TestQueryManyUseCase>(TestQueryManyUseCase::class) {
        override operator fun invoke(useCase: TestQueryManyUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryManyUseCaseInterface : QueryManyUseCase<TestResult> {
        class Handler :
            QueryManyUseCaseHandler<TestResult, TestQueryManyUseCaseInterface>(TestQueryManyUseCaseInterface::class) {
            override operator fun invoke(useCase: TestQueryManyUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestQueryOneUseCase : QueryOneUseCase<TestResult>
    private class TestQueryOneUseCaseHandler :
        QueryOneUseCaseHandler<TestResult, TestQueryOneUseCase>(TestQueryOneUseCase::class) {
        override operator fun invoke(useCase: TestQueryOneUseCase): TestResult {
            return TestResult()
        }
    }

    private interface TestQueryOneUseCaseInterface : QueryOneUseCase<TestResult> {
        class Handler :
            QueryOneUseCaseHandler<TestResult, TestQueryOneUseCaseInterface>(TestQueryOneUseCaseInterface::class) {
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
    @DisplayName("invoke<RequestOne>()")
    inner class RequestOneTest {

        @Test
        fun `Applies operation on use case`() {
            val result = testInstance(TestUseCase(100))
            assertThat(result, equalTo(TestResult(200)))
        }

        private inner class TestUseCase(val data: Int) : RequestOneUseCase<TestResult>{
            override val requestId= RequestId(123)
        }
        private inner class TestUseCaseHandler :
            RequestOneUseCaseHandler<TestResult, TestUseCase>(TestUseCase::class) {
            override fun invoke(useCase: TestUseCase): TestResult {
                return TestResult(useCase.data * 2)
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseHandler())
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
        private inner class TestQueryManyUseCaseHandler : QueryManyUseCaseHandler<TestResult, TestQueryManyUseCase>(
            TestQueryManyUseCase::class
        ) {
            override fun invoke(useCase: TestQueryManyUseCase): List<TestResult> {
                return IntRange(1, useCase.data)
                    .map { TestResult(10.0.pow(it.toDouble()).toInt()) }
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestQueryManyUseCase::class, TestQueryManyUseCaseHandler())
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
        private inner class TestUseCaseHandler : QueryOneUseCaseHandler<TestResult, TestUseCase>(
            TestUseCase::class
        ) {
            override fun invoke(useCase: TestUseCase): TestResult {
                return TestResult(2810)
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseHandler())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter, {}, { NopLogger })
    }

    private inner class TestResult(val data: Int) : DomainObject {
        override fun equals(other: Any?) = data == (other as TestResult).data
        override fun hashCode() = data
    }

}

