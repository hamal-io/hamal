package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaCodeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaCode("""log.info("hamal rocks")"""),
            expectedJson = """{"value":"log.info(\"hamal rocks\")","type":"Code"}"""
        ),
    ).flatten()
}
