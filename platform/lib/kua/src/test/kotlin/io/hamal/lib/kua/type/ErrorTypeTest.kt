package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class ErrorTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = ErrorType(""),
            expectedJson = """{"type":"ErrorType","value":""}"""
        ),
        generateTestCases(
            testInstance = ErrorType("hamal-error-message"),
            expectedJson = """{"type":"ErrorType","value":"hamal-error-message"}"""
        ),
    ).flatten()
}
