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
class UseCaseTest {

    @Nested
    @DisplayName("CommandUseCase")
    inner class CommandUseCasePayloadHandlerTest {

        @Nested
        @DisplayName("BaseImpl")
        inner class BaseImplTest {

            @Test
            fun `Returns result class`() {
                val testInstance = TestUseCase()
                val result = testInstance.resultClass
                assertThat(result, equalTo(TestResult::class))
            }

            @Test
            fun `Returns use case payload`() {
                val testInstance = TestUseCase()
                val result = testInstance.payload
                assertThat(result, equalTo(testUseCasePayload))
            }
        }

        private inner class TestResult
        private inner class TestUseCasePayload : CommandUseCasePayload

        private val testUseCasePayload = TestUseCasePayload()

        private inner class TestUseCase : CommandUseCase.BaseImpl<TestResult, TestUseCasePayload>(
            TestResult::class, testUseCasePayload
        ) {
            override operator fun invoke(useCase: TestUseCasePayload): List<TestResult> {
                return listOf()
            }
        }
    }


    @Nested
    @DisplayName("QueryUseCaseTest")
    inner class QueryUseCasePayloadHandlerTest {
        @Nested
        @DisplayName("BaseImpl")
        inner class BaseImplTest {

            @Test
            fun `Returns result class`() {
                val testInstance = TestUseCase()
                val result = testInstance.resultClass
                assertThat(result, equalTo(TestResult::class))
            }

            @Test
            fun `Returns use case payload`() {
                val testInstance = TestUseCase()
                val result = testInstance.payload
                assertThat(result, equalTo(testUseCasePayload))
            }
        }

        private inner class TestResult
        private inner class TestUseCasePayload : QueryUseCasePayload

        private val testUseCasePayload = TestUseCasePayload()

        private inner class TestUseCase : QueryUseCase.BaseImpl<TestResult, TestUseCasePayload>(
            TestResult::class, testUseCasePayload
        ) {
            override operator fun invoke(useCase: TestUseCasePayload): List<TestResult> {
                return listOf()
            }
        }
    }

    @Nested
    @DisplayName("FetchOneUseCase")
    inner class FetchOneUseCaseTestPayload {
        @Nested
        @DisplayName("BaseImpl")
        inner class BaseImplTest {

            @Test
            fun `Returns result class`() {
                val testInstance = TestUseCase()
                val result = testInstance.resultClass
                assertThat(result, equalTo(TestResult::class))
            }

            @Test
            fun `Returns use case payload class`() {
                val testInstance = TestUseCase()
                val result = testInstance.payload
                assertThat(result, equalTo(testUseCasePayload))
            }

            private inner class TestResult
            private inner class TestUseCasePayload : FetchOneUseCasePayload

            private val testUseCasePayload = TestUseCasePayload()

            private inner class TestUseCase : FetchOneUseCase.BaseImpl<TestResult, TestUseCasePayload>(
                TestResult::class, testUseCasePayload
            ) {
                override operator fun invoke(payload: TestUseCasePayload): Maybe<TestResult> {
                    return Maybe.none()
                }
            }
        }
    }

}