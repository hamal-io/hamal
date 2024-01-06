package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class AnySerializableTypeTest {

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
            expectedJson = """{"type":"ArrayType","value":{"1234":{"type":"StringType","value":"value"}}}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaDecimal("123456789.987654321")),
            expectedJson = """{"type":"DecimalType","value":"123456789.987654321"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaError("SomeErrorMessage")),
            expectedJson = """{"type":"ErrorType","value":"SomeErrorMessage"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaFalse),
            expectedJson = """{"type":"FalseType"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(
                KuaMap(
                    mutableMapOf(
                        "value" to KuaString("hamal")
                    )
                )
            ),
            expectedJson = """{"type":"MapType","value":{"value":{"type":"StringType","value":"hamal"}}}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaNil),
            expectedJson = """{"type":"NilType"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaNumber(42.10)),
            expectedJson = """{"type":"NumberType","value":"42.1"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaString("hamal")),
            expectedJson = """{"type":"StringType","value":"hamal"}"""
        ),
        generateTestCases(
            testInstance = KuaAny(KuaTrue),
            expectedJson = """{"type":"TrueType"}"""
        )
    ).flatten()

}
