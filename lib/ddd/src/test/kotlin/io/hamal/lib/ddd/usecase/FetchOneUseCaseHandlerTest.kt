package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@Nested
class FetchOneUseCaseHandlerTest {

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

    private class TestUseCase : FetchOneUseCase

    private class TestUseCaseHandler : FetchOneUseCaseHandler.BaseImpl<TestResult, TestUseCase>() {
        override fun handle(useCase: TestUseCase): Maybe<TestResult> {
            return Maybe.none()
        }
    }

}