package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class NilTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = NilType,
            expectedJson = """{"type":"NilType"}"""
        ),
    ).flatten()
}
