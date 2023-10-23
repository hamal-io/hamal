package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class MapTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = MapType(),
            expectedJson = """{"type":"MapType"}"""
        ),
        generateTestCases(
            testInstance = MapType(
                mutableMapOf(
                    "key" to StringType("value")
                )
            ),
            expectedJson = """{"type":"MapType","value":{"key":{"type":"StringType","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            testInstance = MapType(
                mutableMapOf(
                    "23" to NumberType(34)
                )
            ),
            expectedJson = """{"type":"MapType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}