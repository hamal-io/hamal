package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.NopLogger
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import java.util.*

class BackendUseCaseRegistryAdapterTest {

    @Nested
    @DisplayName("GetCommandUseCasePort")
    inner class GetCommandUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestCommandUseCase::class]
            assertThat(result, equalTo(testCommandUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestCommandUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestCommandUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestCommandUseCaseInterface::class,
                    testCommandInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestCommandUseCaseInterface::class]
            assertThat(result, equalTo(testCommandInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestCommandUseCase::class, testCommandUseCaseOp)
        }

        private val testCommandUseCaseOp = TestCommandUseCaseOperation()
        private val testCommandInterfaceUseCaseOp = TestCommandUseCaseInterface.Operation()

    }

    @Nested
    @DisplayName("GetQueryUseCasePort")
    inner class GetQueryUseCasePortTestPayloadOperation {
        @Test
        fun `Exists`() {
            val testInstance = testInstanceWithUseCase()

            val result = testInstance[TestQueryUseCase::class]
            assertThat(result, equalTo(testQueryUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestQueryUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestQueryUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestQueryUseCaseInterface::class,
                    testQueryInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestQueryUseCaseInterface::class]
            assertThat(result, equalTo(testQueryInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
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

            val result = testInstance[TestFetchOneUseCase::class]
            assertThat(result, equalTo(testFetchOneUseCaseOp))
        }

        @Test
        fun `Not found`() {
            val testInstance = BackendUseCaseRegistryAdapter()

            val exception = assertThrows<IllegalStateException> {
                testInstance[TestFetchOneUseCase::class]
            }
            assertThat(
                exception.message,
                containsString("No operation registered for class io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapterTest\$TestFetchOneUseCase")
            )
        }

        @Test
        fun `Interface of use case is registered`() {
            val testInstance = BackendUseCaseRegistryAdapter().apply {
                register(
                    TestFetchOneUseCaseInterface::class,
                    testFetchOneInterfaceUseCaseOp
                )
            }
            val result = testInstance[TestFetchOneUseCaseInterface::class]
            assertThat(result, equalTo(testFetchOneInterfaceUseCaseOp))
        }

        private fun testInstanceWithUseCase() = BackendUseCaseRegistryAdapter().apply {
            register(TestFetchOneUseCase::class, testFetchOneUseCaseOp)
        }

        private val testFetchOneUseCaseOp = TestFetchOneUseCaseOperation()
        private val testFetchOneInterfaceUseCaseOp = TestFetchOneUseCaseInterface.Handler()

    }

    private interface TestResultInterface : DomainObject
    private class TestResult : TestResultInterface
    private class IncompatibleTestResult
    private class TestCommandUseCase : CommandUseCase<TestResult>

    private class TestCommandUseCaseOperation :
        CommandUseCaseOperation<TestResult, TestCommandUseCase>(TestCommandUseCase::class) {
        override operator fun invoke(useCase: TestCommandUseCase): TestResult {
            return TestResult()
        }
    }

    private interface TestCommandUseCaseInterface : CommandUseCase<TestResult> {
        class Operation :
            CommandUseCaseOperation<TestResult, TestCommandUseCaseInterface>(TestCommandUseCaseInterface::class) {
            override operator fun invoke(useCase: TestCommandUseCaseInterface): TestResult {
                return TestResult()
            }
        }
    }


    private class TestQueryUseCase : QueryUseCase<TestResult>
    private class TestQueryUseCaseOperation :
        QueryUseCaseOperation<TestResult, TestQueryUseCase>(TestQueryUseCase::class) {
        override operator fun invoke(useCase: TestQueryUseCase): List<TestResult> {
            return listOf()
        }
    }

    private interface TestQueryUseCaseInterface : QueryUseCase<TestResult> {
        class Handler : QueryUseCaseOperation<TestResult, TestQueryUseCaseInterface>(TestQueryUseCaseInterface::class) {
            override operator fun invoke(useCase: TestQueryUseCaseInterface): List<TestResult> {
                return listOf()
            }
        }
    }

    private class TestFetchOneUseCase : FetchOneUseCase<TestResult>
    private class TestFetchOneUseCaseOperation :
        FetchOneUseCaseOperation<TestResult, TestFetchOneUseCase>(TestFetchOneUseCase::class) {
        override operator fun invoke(useCase: TestFetchOneUseCase): TestResult? {
            return null
        }
    }

    private interface TestFetchOneUseCaseInterface : FetchOneUseCase<TestResult> {
        class Handler :
            FetchOneUseCaseOperation<TestResult, TestFetchOneUseCaseInterface>(TestFetchOneUseCaseInterface::class) {
            override operator fun invoke(useCase: TestFetchOneUseCaseInterface): TestResult? {
                return null
            }
        }
    }
}

@Nested
@DisplayName("BackendUseCaseInvokerAdapter")
class BackendUseCaseInvokerAdapterTest {
    @Nested
    @DisplayName("command()")
    inner class CommandTest {
        @Test
        fun `Applies operation on use case`() {
            val result = testInstance.command(TestUseCase(100))
            assertThat(result, equalTo(TestResult(200)))
        }

//            @Test
//            fun `Applies operation without yielding result`() {
//                testInstance.command(
//                    TestNoResultUseCase(),
//                    TestNoResultUseCase(),
//                    TestNoResultUseCase(),
//                )
//                assertThat(ref.get(), equalTo(3))
//            }

        private inner class TestResult(val data: Int) : DomainObject {
            override fun equals(other: Any?) = data == (other as TestResult).data
            override fun hashCode() = data
        }

        private inner class TestUseCase(val data: Int) : CommandUseCase<TestResult>
        private inner class TestUseCaseOperation :
            CommandUseCaseOperation<TestResult, TestUseCase>(TestUseCase::class) {
            override fun invoke(useCase: TestUseCase): TestResult {
                return TestResult(useCase.data * 2)
            }
        }

        //            private inner class TestNoResultUseCase : CommandUseCase
//            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
//                CommandUseCaseOperation<Unit, TestNoResultUseCase>(Unit::class, TestNoResultUseCase::class) {
//                override fun invoke(useCase: TestNoResultUseCase): List<Unit> {
//                    ref.incrementAndGet()
//                    return listOf()
//                }
//            }
//
        private val testRegistryAdapter = BackendUseCaseRegistryAdapter()

        //            private val ref = AtomicInteger()
        init {
            testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
        }

        private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter, {}, { NopLogger })
    }

//        @Nested
//        @DisplayName("query()")
//        inner class QueryTest {
//            @Test
//            fun `Applies operation on each usecase`() {
//                val result = testInstance.query(
//                    Int::class,
//                    TestQueryUseCase(3)
//                )
//                assertThat(result, equalTo(listOf(10, 100, 1000)))
//            }
//
//            private inner class TestQueryUseCase(val data: Int) : QueryUseCase
//            private inner class TestQueryUseCaseOperation : QueryUseCaseOperation<Int, TestQueryUseCase>(
//                Int::class, TestQueryUseCase::class
//            ) {
//                override fun invoke(useCase: TestQueryUseCase): List<Int> {
//                    return IntRange(1, useCase.data)
//                        .map { 10.0.pow(it.toDouble()).toInt() }
//                }
//            }
//
//            private val testRegistryAdapter = BackendUseCaseRegistryAdapter()
//
//            init {
//                testRegistryAdapter.register(TestQueryUseCase::class, TestQueryUseCaseOperation())
//            }
//
//            private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter,{},{NopLogger})
//        }
//
//        @Nested
//        @DisplayName("fetchOne()")
//        inner class FetchOneTest {
//            @Test
//            fun `Applies operation and yields result`() {
//                val result = testInstance.fetchOne(
//                    Int::class,
//                    TestUseCase(3)
//                )
//                assertThat(result, equalTo(2810))
//            }
//
//            @Test
//            fun `Applies operation but does not yield result`() {
//                val result = testInstance.fetchOne(Int::class, TestNoResultUseCase())
//                assertThat(result, nullValue())
//                assertThat(ref.get(), equalTo(1))
//            }
//
//            private inner class TestUseCase(val data: Int) : FetchOneUseCase
//            private inner class TestUseCaseOperation : FetchOneUseCaseOperation<Int, TestUseCase>(
//                Int::class, TestUseCase::class
//            ) {
//                override fun invoke(useCase: TestUseCase): Int? {
//                    return 2810
//                }
//            }
//
//            private inner class TestNoResultUseCase : FetchOneUseCase
//            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
//                FetchOneUseCaseOperation<Int, TestNoResultUseCase>(Int::class, TestNoResultUseCase::class) {
//                override fun invoke(useCase: TestNoResultUseCase): Int? {
//                    ref.incrementAndGet()
//                    return null
//                }
//            }
//
//            private val testRegistryAdapter = BackendUseCaseRegistryAdapter()
//            private val ref = AtomicInteger()
//
//            init {
//                testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
//                testRegistryAdapter.register(TestNoResultUseCase::class, TestNoResultUseCaseOperation(ref))
//            }
//
//            private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter,{},{NopLogger})
//        }
}

