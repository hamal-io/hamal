package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaMapTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaMap(),
            expectedJson = """{"value":{},"type":"Map"}"""
        ),
        generateTestCases(
            testInstance = KuaMap(
                mutableMapOf(
                    "key" to KuaString("value")
                )
            ),
            expectedJson = """{"value":{"key":{"value":"value","type":"String"}},"type":"Map"}""".trimIndent()
        ),
        generateTestCases(
            testInstance = KuaMap(
                mutableMapOf(
                    "23" to KuaNumber(34)
                )
            ),
            expectedJson = """{"value":{"23":{"value":34.0,"type":"Number"}},"type":"Map"}"""
        ),
    ).flatten()
}