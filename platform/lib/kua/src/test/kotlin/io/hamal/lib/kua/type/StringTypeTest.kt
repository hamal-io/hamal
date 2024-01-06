package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class StringTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaString(""),
            expectedJson = """{"type":"StringType","value":""}"""
        ),
        generateTestCases(
            testInstance = KuaString("hamal"),
            expectedJson = """{"type":"StringType","value":"hamal"}"""
        )
    ).flatten()
}
