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
//class UseCaseOperationTest {
//
//    @Nested
//    @DisplayName("ExecuteOneUseCaseOperation")
//    inner class ExecuteOneUseCaseOperationTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//
//        private inner class TestResult
//        private inner class TestUseCase : ExecuteOneUseCase
//        private inner class TestUseCaseOperation : ExecuteOneUseCaseOperation<TestResult, TestUseCase>(
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
//    @DisplayName("QueryManyUseCaseOperation")
//    inner class QueryManyUseCaseOperationTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//        private inner class TestResult
//        private inner class TestUseCase : QueryManyUseCase
//        private inner class TestUseCaseOperation : QueryManyUseCaseOperation<TestResult, TestUseCase>(
//            TestResult::class, TestUseCase::class
//        ) {
//            override operator fun invoke(useCase: TestUseCase): List<TestResult> {
//                return listOf()
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("QueryOneUseCaseOperation")
//    inner class QueryOneUseCaseOperationTest {
//
//        @Test
//        fun `Returns result class`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.resultClass
//            assertThat(result, equalTo(TestResult::class))
//        }
//
//        @Test
//        fun `Returns use case useCase class`() {
//            val testInstance = TestUseCaseOperation()
//            val result = testInstance.useCaseClass
//            assertThat(result, equalTo(TestUseCase::class))
//        }
//
//        private inner class TestResult
//        private inner class TestUseCase : QueryOneUseCase
//        private inner class TestUseCaseOperation : QueryOneUseCaseOperation<TestResult, TestUseCase>(
//            TestResult::class, TestUseCase::class
//        ) {
//            override operator fun invoke(useCase: TestUseCase): TestResult? {
//                return null
//            }
//        }
//    }
//
//}