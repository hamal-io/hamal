package io.hamal.lib.kua.value

import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class StringValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(StringValue("hamal"), """{"type":"StringValue","value":"hamal"}"""),
    ).flatten()
}
