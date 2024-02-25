package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaTableTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaTable.Map(),
            expectedJson = """{"value":{},"type":"Table"}"""
        ),
        generateTestCases(
            testInstance = KuaTable.Map(
                "key" to KuaString("value")
            ),
            expectedJson = """{"value":{"key":{"value":"value","type":"String"}},"type":"Table"}""".trimIndent()
        ),
        generateTestCases(
            testInstance = KuaTable.Map(
                "23" to KuaNumber(34)
            ),
            expectedJson = """{"value":{"23":{"value":34.0,"type":"Number"}},"type":"Table"}"""
        ),

//        generateTestCases(
//            testInstance = KuaTable(
//                mutableMapOf(
//                    "1234" to KuaString("value")
//                )
//            ),
//            expectedJson = """{"value":{"1234":{"value":"value","type":"String"}},"type":"Table"}"""
//        ),
//        generateTestCases(
//            testInstance = KuaTable(
//                mutableMapOf(
//                    "23" to KuaNumber(34)
//                )
//            ),
//            expectedJson = """{"value":{"23":{"value":34.0,"type":"Number"}},"type":"Table"}"""
//        )
        TODO()
    ).flatten()
}