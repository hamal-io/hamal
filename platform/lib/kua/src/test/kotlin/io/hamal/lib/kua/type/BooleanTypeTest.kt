package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class TrueValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(True, """{"type":"TrueType"}"""),
    ).flatten()
}

class FalseValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(False, """{"type":"FalseType"}"""),
    ).flatten()
}