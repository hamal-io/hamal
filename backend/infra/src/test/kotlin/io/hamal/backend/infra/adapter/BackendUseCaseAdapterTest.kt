package io.hamal.backend.infra.adapter

import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import java.util.*
import kotlin.math.pow

@DisplayName("BackendUseCaseRegistryAdapter")
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
    @DisplayName("GetRequestManyUseCasePort")
    inner class GetRequestManyUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestRequestManyUseCase::class]
            assertThat(result, equalTo(testRequestManyUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestRequestManyUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestRequestManyUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestRequestManyUseCaseInterface::class,
                    testRequestManyInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestRequestManyUseCaseInterface::class]
            assertThat(result, equalTo(testRequestManyInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestRequestManyUseCase::class, testRequestManyUseCaseOp)
        }

        private val testRequestManyUseCaseOp = TestRequestManyUseCaseHandler()
        private val testRequestManyInterfaceUseCaseOp = TestRequestManyUseCaseInterface.Operation()

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


    private interface TestResultInterface : DomainObject<TestId>
    private class TestResult(override val id: TestId) : TestResultInterface
    private class TestRequestOneUseCase : RequestOneUseCase<TestResult> {
        override val requestId = RequestId(123)
        override val shard = Shard(23)
    }

    private class TestRequestOneUseCaseHandler :
        RequestOneUseCaseHandler<TestResult, TestRequestOneUseCase>(TestRequestOneUseCase::class) {
        override operator fun invoke(useCase: TestRequestOneUseCase): TestResult {
            return TestResult(TestId(0))
        }
    }

    private interface TestRequestOneUseCaseInterface : RequestOneUseCase<TestResult> {
        class Operation :
            RequestOneUseCaseHandler<TestResult, TestRequestOneUseCaseInterface>(TestRequestOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestRequestOneUseCaseInterface): TestResult {
                return TestResult(TestId(0))
            }
        }
    }

    private class TestRequestManyUseCase : RequestManyUseCase<TestResult> {
        override val requestId = RequestId(123)
        override val shard = Shard(23)
    }

    private class TestRequestManyUseCaseHandler :
        RequestManyUseCaseHandler<TestResult, TestRequestManyUseCase>(TestRequestManyUseCase::class) {
        override operator fun invoke(useCase: TestRequestManyUseCase): List<TestResult> {
            return listOf(TestResult(TestId(0)))
        }
    }

    private interface TestRequestManyUseCaseInterface : RequestManyUseCase<TestResult> {
        class Operation :
            RequestManyUseCaseHandler<TestResult, TestRequestManyUseCaseInterface>(TestRequestManyUseCaseInterface::class) {
            override operator fun invoke(useCase: TestRequestManyUseCaseInterface): List<TestResult> {
                return listOf(TestResult(TestId(0)))
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
            return TestResult(TestId(0))
        }
    }

    private interface TestQueryOneUseCaseInterface : QueryOneUseCase<TestResult> {
        class Handler :
            QueryOneUseCaseHandler<TestResult, TestQueryOneUseCaseInterface>(TestQueryOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestQueryOneUseCaseInterface): TestResult {
                return TestResult(TestId(0))
            }
        }
    }
}

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

        private inner class TestUseCase(val data: Int) : RequestOneUseCase<TestResult> {
            override val requestId = RequestId(123)
            override val shard = Shard(23)
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

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter) {}
    }

    @Nested
    @DisplayName("invoke<RequestMany>()")
    inner class RequestManyTest {

        @Test
        fun `Applies operation on use case`() {
            val result = testInstance(TestUseCase(100))
            assertThat(
                result, equalTo(
                    listOf(
                        TestResult(200),
                        TestResult(400)
                    )
                )
            )
        }

        private inner class TestUseCase(val data: Int) : RequestManyUseCase<TestResult> {
            override val requestId = RequestId(123)
            override val shard = Shard(23)
        }

        private inner class TestUseCaseHandler :
            RequestManyUseCaseHandler<TestResult, TestUseCase>(TestUseCase::class) {
            override fun invoke(useCase: TestUseCase): List<TestResult> {
                return listOf(
                    TestResult(useCase.data * 2),
                    TestResult(useCase.data * 4)
                )
            }
        }

        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseHandler())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter) {}
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

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter) {}
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

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter) {}
    }

    private inner class TestResult(override val id: TestId) : DomainObject<TestId> {
        constructor(id: Int) : this(TestId(id))

        override fun equals(other: Any?) = id == (other as TestResult).id
        override fun hashCode() = id.hashCode()
    }

}

class TestId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}
