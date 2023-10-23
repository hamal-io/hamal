package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class StringTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = StringType(""),
            expectedJson = """{"type":"StringType","value":""}"""
        ),
        generateTestCases(
            testInstance = StringType("hamal"),
            expectedJson = """{"type":"StringType","value":"hamal"}"""
        )
    ).flatten()
}
