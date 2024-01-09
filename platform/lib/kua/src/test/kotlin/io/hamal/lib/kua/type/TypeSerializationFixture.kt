package io.hamal.lib.kua.type

import io.hamal.lib.domain.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

internal object TypeSerializationFixture {

    fun generateTestCases(
        testInstance: KuaType,
        expectedJson: String
    ): List<DynamicTest> {
        return listOf(
            dynamicTest("${testInstance::class.simpleName}") {
                val encoded = Json.serialize(testInstance)
                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
                val decoded = Json.deserialize(testInstance::class, encoded)
                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))
            }
        )
    }

    fun generateTestCases(
        testInstance: KuaAny,
        expectedJson: String
    ): List<DynamicTest> {
        return listOf(
            dynamicTest("${testInstance.value::class.simpleName}") {
                val encoded = Json.serialize(testInstance)
                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
                val decoded = Json.deserialize(KuaAny::class, encoded)
                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))

            }
        )
    }
}