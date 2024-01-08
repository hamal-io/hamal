package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaAnyTest {

    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaAny(
                KuaArray(
                    mutableMapOf(
                        1234 to KuaString("value")
                    )
                )
            ),
            expectedJson = """{"value":{"1234":{"value":"value","type":"String"}},"type":"Array"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaDecimal("123456789.987654321")),
            expectedJson = """{"value":"123456789.987654321","type":"Decimal"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaError("SomeErrorMessage")),
            expectedJson = """{"value":"SomeErrorMessage","type":"Error"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaFalse),
            expectedJson = """{"value":false,"type":"Boolean"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(
                KuaMap(
                    mutableMapOf(
                        "key" to KuaString("hamal")
                    )
                )
            ),
            expectedJson = """{"value":{"key":{"value":"hamal","type":"String"}},"type":"Map"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaNil),
            expectedJson = """{"type":"Nil"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaNumber(42.10)),
            expectedJson = """{"value":42.1,"type":"Number"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaString("hamal")),
            expectedJson = """{"value":"hamal","type":"String"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaTrue),
            expectedJson = """{"value":true,"type":"Boolean"}"""
        )
    ).flatten()

}
