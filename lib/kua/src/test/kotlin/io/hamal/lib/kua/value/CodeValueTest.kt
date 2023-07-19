package io.hamal.lib.kua.value

import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class CodeValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            CodeValue("log.info('hamal rocks')"),
            """{"type":"CodeValue","value":"log.info('hamal rocks')"}"""
        ),
    ).flatten()
}
