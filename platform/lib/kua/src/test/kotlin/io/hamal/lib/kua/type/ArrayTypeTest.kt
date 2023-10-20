package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class ArrayTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(ArrayType(), """{"type":"ArrayType"}"""),
        generateTestCases(
            ArrayType(
                mutableMapOf(
                    1234 to StringType("value")
                )
            ),
            """{"type":"ArrayType","value":{"1234":{"type":"StringType","value":"value"}}}"""
        ),
        generateTestCases(
            ArrayType(
                mutableMapOf(
                    23 to NumberType(34)
                )
            ),
            """{"type":"ArrayType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}