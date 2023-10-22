package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory


class NumberTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(NumberType(42), """{"type":"NumberType","value":"42"}"""),
        generateTestCases(NumberType(42.24), """{"type":"NumberType","value":"42.24"}"""),
    ).flatten()
}
