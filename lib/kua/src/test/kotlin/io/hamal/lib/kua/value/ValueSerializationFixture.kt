@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.lib.kua.value

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

internal object ValueSerializationFixture {

    fun generateTestCases(
        testInstance: SerializableValue,
        expectedJson: String
    ): List<DynamicTest> {
        return listOf(
            dynamicTest("${testInstance::class.simpleName} supports protobuf serializer") {
                val encoded = ProtoBuf.encodeToByteArray(testInstance)
                val decoded = ProtoBuf.decodeFromByteArray<SerializableValue>(encoded)
                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))
            },
            dynamicTest("${testInstance::class.simpleName} supports json serializer") {
                val encoded = Json.encodeToString(testInstance)
                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
                val decoded = Json.decodeFromString<SerializableValue>(encoded)
                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))
            }
        )
    }
}