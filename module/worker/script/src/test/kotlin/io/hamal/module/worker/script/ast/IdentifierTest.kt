package io.hamal.module.worker.script.ast

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierTest : AbstractAstTest() {

    @Nested
    @DisplayName("Identifier.Parser")
    inner class IdentifierParserTest {
        @Test
        fun `Parses identifier`() {
            val result = parse(Identifier.Parser, "some_variable")
            result.verifyPrecedence("(some_variable)")
        }
    }

}