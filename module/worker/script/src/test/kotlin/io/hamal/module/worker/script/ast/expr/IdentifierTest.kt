package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IdentifierTest : AbstractAstTest() {
    @Nested
    @DisplayName("Identifier.ParseIdentifier")
    inner class IdentifierParserTest {
        @Test
        fun `Parses identifier`() {
            val result = parseSimpleLiteralExpression(Identifier.ParseIdentifier, "some_variable")
            result.verifyPrecedence("some_variable")
        }
    }
}