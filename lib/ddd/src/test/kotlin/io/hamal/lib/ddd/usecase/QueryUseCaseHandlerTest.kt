package io.hamal.lib.ddd.usecase

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class QueryUseCaseHandlerTest {

    @Nested
    @DisplayName("BaseImpl")
    inner class BaseImplTest {

        @Test
        fun `ResultClass gets calculated correctly`() {
            val testInstance = TestUseCaseHandler()
            val result = testInstance.resultClass
            assertThat(result, equalTo(TestResult::class.java))
        }

        @Test
        fun `UseCaseClass gets calculated correctly`() {
            val testInstance = TestUseCaseHandler()
            val result = testInstance.useCaseClass
            assertThat(result, equalTo(TestUseCase::class.java))
        }
    }

    private class TestResult
    private class TestUseCase : QueryUseCase
    private class TestUseCaseHandler : QueryUseCaseHandler.BaseImpl<TestResult, TestUseCase>() {
        override fun handle(useCase: TestUseCase): List<TestResult> {
            return listOf()
        }
    }
}