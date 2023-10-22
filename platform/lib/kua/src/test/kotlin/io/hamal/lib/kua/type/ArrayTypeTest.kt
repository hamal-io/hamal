package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class ArrayTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = ArrayType(),
            expectedJson = """{"type":"ArrayType"}"""
        ),
        generateTestCases(
            testInstance = ArrayType(
                mutableMapOf(
                    1234 to StringType("value")
                )
            ),
            expectedJson = """{"type":"ArrayType","value":{"1234":{"type":"StringType","value":"value"}}}"""
        ),
        generateTestCases(
            testInstance = ArrayType(
                mutableMapOf(
                    23 to NumberType(34)
                )
            ),
            expectedJson = """{"type":"ArrayType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}