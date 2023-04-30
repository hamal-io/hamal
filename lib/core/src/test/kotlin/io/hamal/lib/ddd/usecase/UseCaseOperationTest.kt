//package io.hamal.lib.ddd.usecase
//
//import io.hamal.lib.ddd.usecase.*
//import org.hamcrest.CoreMatchers.*
//import org.hamcrest.MatcherAssert.assertThat
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import java.util.*
//
//@Nested
//class UseCaseHandlerTest {
//
//    @Nested
//    @DisplayName("RequestOneUseCaseHandler")
//    inner class RequestOneUseCaseHandlerTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//
//        private inner class TestResult
//        private inner class TestUseCase : RequestOneUseCase
//        private inner class TestUseCaseHandler : RequestOneUseCaseHandler<TestResult, TestUseCase>(
//            TestResult::class, TestUseCase::class
//        ) {
//            override operator fun invoke(useCase: TestUseCase): List<TestResult> {
//                return listOf()
//            }
//        }
//    }
//
//
//    @Nested
//    @DisplayName("QueryManyUseCaseHandler")
//    inner class QueryManyUseCaseHandlerTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//        private inner class TestResult
//        private inner class TestUseCase : QueryManyUseCase
//        private inner class TestUseCaseHandler : QueryManyUseCaseHandler<TestResult, TestUseCase>(
//            TestResult::class, TestUseCase::class
//        ) {
//            override operator fun invoke(useCase: TestUseCase): List<TestResult> {
//                return listOf()
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("QueryOneUseCaseHandler")
//    inner class QueryOneUseCaseHandlerTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase class`() {
//            val testInstance = TestUseCaseHandler()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//        private inner class TestResult
//        private inner class TestUseCase : QueryOneUseCase
//        private inner class TestUseCaseHandler : QueryOneUseCaseHandler<TestResult, TestUseCase>(
//            TestResult::class, TestUseCase::class
//        ) {
//            override operator fun invoke(useCase: TestUseCase): TestResult? {
//                return null
//            }
//        }
//    }
//
//}