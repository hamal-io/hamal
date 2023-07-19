package io.hamal.lib.kua.value

import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class TrueValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(TrueValue, """{"type":"TrueValue"}"""),
    ).flatten()
}

class FalseValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(FalseValue, """{"type":"FalseValue"}"""),
    ).flatten()
}