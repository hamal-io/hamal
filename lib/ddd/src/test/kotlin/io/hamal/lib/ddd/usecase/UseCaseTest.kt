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
    inner class CommandUseCaseTest {

        @Nested
        @DisplayName("CommandUseCaseHandler")
        inner class CommandUseCaseHandlerTEst {

            @Nested
            @DisplayName("BaseImpl")
            inner class BaseImplTest {

                @Test
                fun `Result class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.resultClass
                    assertThat(result, equalTo(TestResult::class.java))
                }

                @Test
                fun `Usecase class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.useCaseClass
                    assertThat(result, equalTo(TestUseCase::class.java))
                }
            }

            private inner class TestResult
            private inner class TestUseCase : CommandUseCase
            private inner class TestUseCaseHandler : CommandUseCaseHandler.BaseImpl<TestResult, TestUseCase>() {
                override fun handle(useCase: TestUseCase): List<TestResult> {
                    return listOf()
                }
            }
        }
    }

    @Nested
    @DisplayName("QueryUseCase")
    inner class QueryUseCaseTest {

        @Nested
        @DisplayName("QueryUseCaseHandlerTest")
        inner class QueryUseCaseHandlerTest {
            @Nested
            @DisplayName("BaseImpl")
            inner class BaseImplTest {

                @Test
                fun `Result class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.resultClass
                    assertThat(result, equalTo(TestResult::class.java))
                }

                @Test
                fun `Usecase class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.useCaseClass
                    assertThat(result, equalTo(TestUseCase::class.java))
                }
            }

            private inner class TestResult
            private inner class TestUseCase : QueryUseCase
            private inner class TestUseCaseHandler : QueryUseCaseHandler.BaseImpl<TestResult, TestUseCase>() {
                override fun handle(useCase: TestUseCase): List<TestResult> {
                    return listOf()
                }
            }
        }

    }

    @Nested
    @DisplayName("FetchOneUseCase")
    inner class FetchOneUseCaseTest {

        @Nested
        @DisplayName("FetchOneUseCaseHandler")
        inner class FetchOneUseCaseHandlerTest {
            @Nested
            @DisplayName("BaseImpl")
            inner class BaseImplTest {

                @Test
                fun `Result class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.resultClass
                    assertThat(result, equalTo(TestResult::class.java))
                }

                @Test
                fun `Usecase class gets calculated correctly`() {
                    val testInstance = TestUseCaseHandler()
                    val result = testInstance.useCaseClass
                    assertThat(result, equalTo(TestUseCase::class.java))
                }
            }

            private inner class TestResult

            private inner class TestUseCase : FetchOneUseCase

            private inner class TestUseCaseHandler : FetchOneUseCaseHandler.BaseImpl<TestResult, TestUseCase>() {
                override fun handle(useCase: TestUseCase): Maybe<TestResult> {
                    return Maybe.none()
                }
            }
        }
    }
}