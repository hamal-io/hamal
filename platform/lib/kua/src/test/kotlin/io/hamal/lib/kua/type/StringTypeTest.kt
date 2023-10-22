package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class StringTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(StringType(""), """{"type":"StringType","value":""}"""),
        generateTestCases(StringType("hamal"), """{"type":"StringType","value":"hamal"}"""),
    ).flatten()
}
