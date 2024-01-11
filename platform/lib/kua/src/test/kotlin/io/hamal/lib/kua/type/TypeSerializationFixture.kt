package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
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
                val encoded = json.serialize(testInstance)
                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
                val decoded = json.deserialize(testInstance::class, encoded)
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
                val encoded = json.serialize(testInstance)
                assertThat("Expects json encoding: $expectedJson", encoded, equalTo(expectedJson))
                val decoded = json.deserialize(KuaAny::class, encoded)
                assertThat("Decoding an encoded value must be equal to testInstance", decoded, equalTo(testInstance))

            }
        )
    }

    private val json = Json(
        JsonFactoryBuilder()
            .register(HotJsonModule)
            .register(KuaJsonModule)
            .register(ValueObjectJsonModule)
    )
}