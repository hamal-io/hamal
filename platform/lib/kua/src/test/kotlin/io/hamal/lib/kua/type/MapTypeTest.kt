package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class MapTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(MapType(), """{"type":"MapType"}"""),
        generateTestCases(
            MapType(
                mutableMapOf(
                    "key" to StringType("value")
                )
            ),
            """{"type":"MapType","value":{"key":{"type":"StringType","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            MapType(
                mutableMapOf(
                    "23" to NumberType(34)
                )
            ),
            """{"type":"MapType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}