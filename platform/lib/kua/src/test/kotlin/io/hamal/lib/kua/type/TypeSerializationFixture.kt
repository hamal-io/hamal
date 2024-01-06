package io.hamal.lib.kua.type

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

internal object TypeSerializationFixture {

    fun generateTestCases(
        testInstance: KuaType,
        expectedJson: String
    ): List<DynamicTest> {
        return listOf(
            dynamicTest("${testInstance::class.simpleName} supports json serializer") {
//                val encoded = Json.encodeToString(testInstance)
//                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
//                val decoded = Json.decodeFromString<SerializableType>(encoded)
//                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))
            }
        )
    }

    fun generateTestCases(
        testInstance: KuaAny,
        expectedJson: String
    ): List<DynamicTest> {
        return listOf(
            dynamicTest("${testInstance.value::class.simpleName} supports json deserializer") {
//                val encoded = Json.encodeToString(testInstance)
//                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
//                val decoded = Json.decodeFromString<AnySerializableType>(encoded)
//                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))
                TODO()
            }
        )
    }
}