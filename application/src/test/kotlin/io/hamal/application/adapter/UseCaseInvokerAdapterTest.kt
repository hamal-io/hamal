package io.hamal.application.adapter

import io.hamal.lib.ddd.usecase.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

@Nested
class UseCaseInvokerAdapterTest {

    @Nested
    @DisplayName("DefaultUseCaseInvokerAdapter")
    inner class DefaultUseCaseInvokerAdapterTest {

        @Nested
        @DisplayName("command()")
        inner class CommandTest {
            @Test
            fun `Applies operation on each usecase`() {
                val result = testInstance.command(
                    Int::class,
                    TestUseCase(10),
                    TestUseCase(100),
                )
                assertThat(result, equalTo(listOf(20, 200)))
            }

            @Test
            fun `Applies operation without yielding result`() {
                testInstance.command(
                    TestNoResultUseCase(),
                    TestNoResultUseCase(),
                    TestNoResultUseCase(),
                )
                assertThat(ref.get(), equalTo(3))
            }

            private inner class TestUseCase(val data: Int) : CommandUseCase
            private inner class TestUseCaseOperation : CommandUseCaseOperation<Int, TestUseCase>(
                Int::class, TestUseCase::class
            ) {
                override fun invoke(useCase: TestUseCase): List<Int> {
                    return listOf(useCase.data * 2)
                }
            }

            private inner class TestNoResultUseCase : CommandUseCase
            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
                CommandUseCaseOperation<Unit, TestNoResultUseCase>(Unit::class, TestNoResultUseCase::class) {
                override fun invoke(useCase: TestNoResultUseCase): List<Unit> {
                    ref.incrementAndGet()
                    return listOf()
                }
            }

            private val testRegistryAdapter = DefaultUseCaseRegistryAdapter()
            private val ref = AtomicInteger()

            init {
                testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
                testRegistryAdapter.register(
                    TestNoResultUseCase::class,
                    TestNoResultUseCaseOperation(ref)
                )
            }

            private val testInstance = DefaultUseCaseInvokerAdapter(testRegistryAdapter)
        }

        @Nested
        @DisplayName("query()")
        inner class QueryTest {
            @Test
            fun `Applies operation on each usecase`() {
                val result = testInstance.query(
                    Int::class,
                    TestQueryUseCase(3)
                )
                assertThat(result, equalTo(listOf(10, 100, 1000)))
            }

            private inner class TestQueryUseCase(val data: Int) : QueryUseCase
            private inner class TestQueryUseCaseOperation : QueryUseCaseOperation<Int, TestQueryUseCase>(
                Int::class, TestQueryUseCase::class
            ) {
                override fun invoke(useCase: TestQueryUseCase): List<Int> {
                    return IntRange(1, useCase.data)
                        .map { 10.0.pow(it.toDouble()).toInt() }
                }
            }

            private val testRegistryAdapter = DefaultUseCaseRegistryAdapter()

            init {
                testRegistryAdapter.register(TestQueryUseCase::class, TestQueryUseCaseOperation())
            }

            private val testInstance = DefaultUseCaseInvokerAdapter(testRegistryAdapter)
        }

        @Nested
        @DisplayName("fetchOne()")
        inner class FetchOneTest {
            @Test
            fun `Applies operation and yields result`() {
                val result = testInstance.fetchOne(
                    Int::class,
                    TestUseCase(3)
                )
                assertThat(result, equalTo(2810))
            }

            @Test
            fun `Applies operation but does not yield result`() {
                val result = testInstance.fetchOne(Int::class, TestNoResultUseCase())
                assertThat(result, nullValue())
                assertThat(ref.get(), equalTo(1))
            }

            private inner class TestUseCase(val data: Int) : FetchOneUseCase
            private inner class TestUseCaseOperation : FetchOneUseCaseOperation<Int, TestUseCase>(
                Int::class, TestUseCase::class
            ) {
                override fun invoke(useCase: TestUseCase): Int? {
                    return 2810
                }
            }

            private inner class TestNoResultUseCase : FetchOneUseCase
            private inner class TestNoResultUseCaseOperation(val ref: AtomicInteger) :
                FetchOneUseCaseOperation<Int, TestNoResultUseCase>(Int::class, TestNoResultUseCase::class) {
                override fun invoke(useCase: TestNoResultUseCase): Int? {
                    ref.incrementAndGet()
                    return null
                }
            }

            private val testRegistryAdapter = DefaultUseCaseRegistryAdapter()
            private val ref = AtomicInteger()

            init {
                testRegistryAdapter.register(TestUseCase::class, TestUseCaseOperation())
                testRegistryAdapter.register(TestNoResultUseCase::class, TestNoResultUseCaseOperation(ref))
            }

            private val testInstance = DefaultUseCaseInvokerAdapter(testRegistryAdapter)
        }
    }

}