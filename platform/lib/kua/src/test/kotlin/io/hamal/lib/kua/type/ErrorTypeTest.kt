package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class ErrorTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(ErrorType(""), """{"type":"ErrorType","value":""}"""),
        generateTestCases(ErrorType("hamal-error-message"), """{"type":"ErrorType","value":"hamal-error-message"}"""),
    ).flatten()
}
