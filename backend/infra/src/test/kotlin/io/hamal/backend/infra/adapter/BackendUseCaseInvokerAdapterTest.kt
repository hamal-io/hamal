//package io.hamal.backend.infra.adapter
//
//import io.hamal.backend.core.port.NopLogger
//import io.hamal.lib.ddd.usecase.*
//import org.hamcrest.CoreMatchers.equalTo
//import org.hamcrest.CoreMatchers.nullValue
//import org.hamcrest.MatcherAssert.assertThat
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import java.util.concurrent.atomic.AtomicInteger
//import kotlin.math.pow
//
//@Nested
//class BackendUseCaseInvokerAdapterTest {
//
//    @Nested
//    @DisplayName("BackendUseCaseInvokerAdapter")
//    inner class BackendUseCaseInvokerAdapterTest {
//
//        @Nested
//        @DisplayName("requestOne()")
//        inner class RequestOneTest {
//            @Test
//            fun `Applies operation on each usecase`() {
//                val result = testInstance.requestOne(
//                    Int::class,
//                    TestUseCase(10),
//                    TestUseCase(100),
//                )
//                assertThat(result, equalTo(listOf(20, 200)))
//            }
//
//            @Test
//            fun `Applies operation without yielding result`() {
//                testInstance.requestOne(
//                    TestNoResultUseCase(),
//                    TestNoResultUseCase(),
//                    TestNoResultUseCase(),
//                )
//                assertThat(ref.get(), equalTo(3))
//            }
//
//            private inner class TestUseCase(val data: Int) : RequestOneUseCase
//            private inner class TestUseCaseOperation : RequestOneUseCaseOperation<Int, TestUseCase>(
//                Int::class, TestUseCase::class
//            ) {
//                override fun invoke(useCase: TestUseCase): List<Int> {
//                    return listOf(useCase.data * 2)
//                }
//            }
//
//            private inner class TestNoResultUseCase : RequestOneUseCase
//            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
//                RequestOneUseCaseOperation<Unit, TestNoResultUseCase>(Unit::class, TestNoResultUseCase::class) {
//                override fun invoke(useCase: TestNoResultUseCase): List<Unit> {
//                    ref.incrementAndGet()
//                    return listOf()
//                }
//            }
//
//            private val testRegistryAdapter = BackendUseCaseRegistryAdapter()
//            private val ref = AtomicInteger()
//
//            init {
//                testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
//                testRegistryAdapter.register(
//                    TestNoResultUseCase::class,
//                    TestNoResultUseCaseOperation(ref)
//                )
//            }
//
//            private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter,{}, {NopLogger})
//        }
//
//        @Nested
//        @DisplayName("queryMany()")
//        inner class QueryManyTest {
//            @Test
//            fun `Applies operation on each usecase`() {
//                val result = testInstance.queryMany(
//                    Int::class,
//                    TestQueryManyUseCase(3)
//                )
//                assertThat(result, equalTo(listOf(10, 100, 1000)))
//            }
//
//            private inner class TestQueryManyUseCase(val data: Int) : QueryManyUseCase
//            private inner class TestQueryManyUseCaseOperation : QueryManyUseCaseOperation<Int, TestQueryManyUseCase>(
//                Int::class, TestQueryManyUseCase::class
//            ) {
//                override fun invoke(useCase: TestQueryManyUseCase): List<Int> {
//                    return IntRange(1, useCase.data)
//                        .map { 10.0.pow(it.toDouble()).toInt() }
//                }
//            }
//
//            private val testRegistryAdapter = BackendUseCaseRegistryAdapter()
//
//            init {
//                testRegistryAdapter.register(TestQueryManyUseCase::class, TestQueryManyUseCaseOperation())
//            }
//
//            private val testInstance = BackendUseCaseInvokerAdapter(testRegistryAdapter,{},{NopLogger})
//        }
//
//        @Nested
//        @DisplayName("queryOne()")
//        inner class QueryOneTest {
//            @Test
//            fun `Applies operation and yields result`() {
//                val result = testInstance.queryOne(
//                    Int::class,
//                    TestUseCase(3)
//                )
//                assertThat(result, equalTo(2810))
//            }
//
//            @Test
//            fun `Applies operation but does not yield result`() {
//                val result = testInstance.queryOne(Int::class, TestNoResultUseCase())
//                assertThat(result, nullValue())
//                assertThat(ref.get(), equalTo(1))
//            }
//
//            private inner class TestUseCase(val data: Int) : QueryOneUseCase
//            private inner class TestUseCaseOperation : QueryOneUseCaseOperation<Int, TestUseCase>(
//                Int::class, TestUseCase::class
//            ) {
//                override fun invoke(useCase: TestUseCase): Int? {
//                    return 2810
//                }
//            }
//
//            private inner class TestNoResultUseCase : QueryOneUseCase
//            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
//                QueryOneUseCaseOperation<Int, TestNoResultUseCase>(Int::class, TestNoResultUseCase::class) {
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
//    }
//
//}