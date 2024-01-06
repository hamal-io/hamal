package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class NumberTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaNumber(42),
            expectedJson = """{"type":"NumberType","value":"42"}"""
        ),
        generateTestCases(
            testInstance = KuaNumber(42.24),
            expectedJson = """{"type":"NumberType","value":"42.24"}"""
        ),
    ).flatten()
}
