package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaArrayTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaArray(),
            expectedJson = """{"value":{},"type":"Array"}"""
        ),
        generateTestCases(
            testInstance = KuaArray(
                mutableMapOf(
                    1234 to KuaString("value")
                )
            ),
            expectedJson = """{"value":{"1234":{"value":"value","type":"String"}},"type":"Array"}"""
        ),
        generateTestCases(
            testInstance = KuaArray(
                mutableMapOf(
                    23 to KuaNumber(34)
                )
            ),
            expectedJson = """{"value":{"23":{"value":34.0,"type":"Number"}},"type":"Array"}"""
        ),
    ).flatten()
}