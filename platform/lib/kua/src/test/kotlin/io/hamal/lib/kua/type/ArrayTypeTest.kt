package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class ArrayTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaArray(),
            expectedJson = """{"type":"ArrayType"}"""
        ),
        generateTestCases(
            testInstance = KuaArray(
                mutableMapOf(
                    1234 to KuaString("value")
                )
            ),
            expectedJson = """{"type":"ArrayType","value":{"1234":{"type":"StringType","value":"value"}}}"""
        ),
        generateTestCases(
            testInstance = KuaArray(
                mutableMapOf(
                    23 to KuaNumber(34)
                )
            ),
            expectedJson = """{"type":"ArrayType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}