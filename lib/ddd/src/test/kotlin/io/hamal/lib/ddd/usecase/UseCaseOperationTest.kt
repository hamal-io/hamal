package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@Nested
class UseCaseOperationTest {

    @Nested
    @DisplayName("CommandUseCaseOperation")
    inner class CommandUseCaseOperationTest {

        @Test
        fun `Returns result class`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.resultClass
            assertThat(result, equalTo(TestResult::class))
        }

        @Test
        fun `Returns use case useCase`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class))
        }


        private inner class TestResult
        private inner class TestUseCase : CommandUseCase
        private inner class TestUseCaseOperation : CommandUseCaseOperation<TestResult, TestUseCase>(
            TestResult::class, TestUseCase::class
        ) {
            override operator fun invoke(useCase: TestUseCase): List<TestResult> {
                return listOf()
            }
        }
    }


    @Nested
    @DisplayName("QueryUseCaseOperation")
    inner class QueryUseCaseOperationTest {

        @Test
        fun `Returns result class`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.resultClass
            assertThat(result, equalTo(TestResult::class))
        }

        @Test
        fun `Returns use case useCase`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class))
        }

        private inner class TestResult
        private inner class TestUseCase : QueryUseCase
        private inner class TestUseCaseOperation : QueryUseCaseOperation<TestResult, TestUseCase>(
            TestResult::class, TestUseCase::class
        ) {
            override operator fun invoke(useCase: TestUseCase): List<TestResult> {
                return listOf()
            }
        }
    }

    @Nested
    @DisplayName("FetchOneUseCaseOperation")
    inner class FetchOneUseCaseOperationTest {

        @Test
        fun `Returns result class`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.resultClass
            assertThat(result, equalTo(TestResult::class))
        }

        @Test
        fun `Returns use case useCase class`() {
            val testInstance = TestUseCaseOperation()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class))
        }

        private inner class TestResult
        private inner class TestUseCase : FetchOneUseCase
        private inner class TestUseCaseOperation : FetchOneUseCaseOperation<TestResult, TestUseCase>(
            TestResult::class, TestUseCase::class
        ) {
            override operator fun invoke(useCase: TestUseCase): Maybe<TestResult> {
                return Maybe.none()
            }
        }
    }

}