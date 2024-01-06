package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class TrueTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaTrue,
            expectedJson = """{"type":"TrueType"}"""
        ),
    ).flatten()
}

class FalseTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaFalse,
            expectedJson = """{"type":"FalseType"}"""
        ),
    ).flatten()
}