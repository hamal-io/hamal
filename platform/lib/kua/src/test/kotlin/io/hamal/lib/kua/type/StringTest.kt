package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class KuaStringTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaString(""),
            expectedJson = """{"value":"","type":"String"}"""
        ),
        generateTestCases(
            testInstance = KuaString("hamal"),
            expectedJson = """{"value":"hamal","type":"String"}"""
        )
    ).flatten()
}
