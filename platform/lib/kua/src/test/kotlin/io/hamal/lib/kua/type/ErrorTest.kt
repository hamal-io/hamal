package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class KuaErrorTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaError(""),
            expectedJson = """{"value":"","type":"Error"}"""
        ),
        generateTestCases(
            testInstance = KuaError("hamal-error-message"),
            expectedJson = """{"value":"hamal-error-message","type":"Error"}"""
        ),
    ).flatten()
}
