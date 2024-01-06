package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class MapTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaMap(),
            expectedJson = """{"type":"MapType"}"""
        ),
        generateTestCases(
            testInstance = KuaMap(
                mutableMapOf(
                    "key" to KuaString("value")
                )
            ),
            expectedJson = """{"type":"MapType","value":{"key":{"type":"StringType","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            testInstance = KuaMap(
                mutableMapOf(
                    "23" to KuaNumber(34)
                )
            ),
            expectedJson = """{"type":"MapType","value":{"23":{"type":"NumberType","value":"34"}}}"""
        ),
    ).flatten()
}