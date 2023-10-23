package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class AnySerializableTypeTest {

    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = AnySerializableType(
                ArrayType(
                    mutableMapOf(
                        1234 to StringType("value")
                    )
                )
            ),
            expectedJson = """{"type":"ArrayType","value":{"1234":{"type":"StringType","value":"value"}}}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(DecimalType("123456789.987654321")),
            expectedJson = """{"type":"DecimalType","value":"123456789.987654321"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(ErrorType("SomeErrorMessage")),
            expectedJson = """{"type":"ErrorType","value":"SomeErrorMessage"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(False),
            expectedJson = """{"type":"FalseType"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(
                MapType(
                    mutableMapOf(
                        "value" to StringType("hamal")
                    )
                )
            ),
            expectedJson = """{"type":"MapType","value":{"value":{"type":"StringType","value":"hamal"}}}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(NilType),
            expectedJson = """{"type":"NilType"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(NumberType(42.10)),
            expectedJson = """{"type":"NumberType","value":"42.1"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(StringType("hamal")),
            expectedJson = """{"type":"StringType","value":"hamal"}"""
        ),
        generateTestCases(
            testInstance = AnySerializableType(True),
            expectedJson = """{"type":"TrueType"}"""
        )
    ).flatten()

}
