package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class KuaNumberTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaNumber(42),
            expectedJson = """{"value":42.0,"type":"Number"}"""
        ),
        generateTestCases(
            testInstance = KuaNumber(42.24),
            expectedJson = """{"value":42.24,"type":"Number"}"""
        ),
    ).flatten()
}
