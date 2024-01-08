package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaNilTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaNil,
            expectedJson = """{"type":"Nil"}"""
        ),
    ).flatten()
}
