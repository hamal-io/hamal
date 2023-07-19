package io.hamal.lib.kua.value

import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class NilValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(NilValue, """{"type":"NilValue"}"""),
    ).flatten()
}
