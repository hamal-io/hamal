package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class CodeTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            CodeType("log.info('hamal rocks')"),
            """{"type":"CodeType","value":"log.info('hamal rocks')"}"""
        ),
    ).flatten()
}
