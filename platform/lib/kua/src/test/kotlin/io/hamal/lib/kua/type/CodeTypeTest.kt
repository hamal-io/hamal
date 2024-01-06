package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class CodeTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaCode("log.info('hamal rocks')"),
            expectedJson = """{"type":"CodeType","value":"log.info('hamal rocks')"}"""
        ),
    ).flatten()
}
