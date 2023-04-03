package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.usecase.CommandUseCaseHandler.*
import io.hamal.lib.meta.Maybe
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@Nested
class CommandUseCaseHandlerTest {

    @Nested
    @DisplayName("MaybeImpl")
    inner class MaybeImplTest {
        @Test
        fun `ResultClass gets calculated correctly`() {
            val testInstance = MaybeCommandUseCaseHandler()
            val result = testInstance.resultClass
            assertThat(result, equalTo(Maybe::class.java))
        }

        @Test
        fun `InnerResultClass gets calculated correctly`() {
            val testInstance = MaybeCommandUseCaseHandler()
            val result = testInstance.innerResultClass
            assertThat(result, equalTo(TestResult::class.java))
        }

        @Test
        fun `UseCaseClass gets calculated correctly`() {
            val testInstance = MaybeCommandUseCaseHandler()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class.java))
        }

        private inner class MaybeCommandUseCaseHandler : MaybeImpl<TestResult, TestUseCase>() {
            override fun handle(useCase: TestUseCase): Maybe<TestResult> {
                throw RuntimeException()
            }
        }
    }


    @Nested
    @DisplayName("ListImpl")
    inner class ListImplTest {
        @Test
        fun `ResultClass gets calculated correctly`() {
            val testInstance = ListCommandUseCaseHandler()
            val result = testInstance.resultClass
            assertThat(result, equalTo(List::class.java))
        }

        @Test
        fun `InnerResultClass gets calculated correctly`() {
            val testInstance = ListCommandUseCaseHandler()
            val result = testInstance.innerResultClass
            assertThat(result, equalTo(TestResult::class.java))
        }

        @Test
        fun `UseCaseClass gets calculated correctly`() {
            val testInstance = ListCommandUseCaseHandler()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class.java))
        }

        private inner class ListCommandUseCaseHandler : ListImpl<TestResult, TestUseCase>() {
            override fun handle(useCase: TestUseCase): List<TestResult> {
                throw RuntimeException()
            }
        }
    }

    @Nested
    @DisplayName("NoResultImpl")
    inner class NoResultImplTest {
        @Test
        fun `ResultClass gets calculated correctly`() {
            val testInstance = NoResultCommandUseCaseHandler()
            val result = testInstance.resultClass
            assertThat(result, equalTo(Unit.javaClass))
        }

        @Test
        fun `UseCaseClass gets calculated correctly`() {
            val testInstance = NoResultCommandUseCaseHandler()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class.java))
        }

        private inner class NoResultCommandUseCaseHandler : NoResultImpl<TestUseCase>() {
            override fun handleWithoutResult(useCase: TestUseCase) {
                throw RuntimeException()
            }
        }
    }


    private interface TestResultInterface

    private class TestResult : TestResultInterface

    private class TestUseCase : CommandUseCase
}