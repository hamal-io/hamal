package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractAstTest() {

    @Nested
    @DisplayName("StringLiteral")
    inner class StringLiteralTest {
        @Test
        fun `Parses string token`() {
            val result = parseExpression(StringLiteral.ParseStringLiteral, "'hello hamal'")
            result.verifyPrecedence("('hello hamal')")
        }
    }

    @Nested
    @DisplayName("TrueLiteral")
    inner class TrueLiteralTest {
        @Test
        fun `Parse true`() {
            val result = parseExpression(TrueLiteral.ParseTrueLiteral, "true")
            result.verifyPrecedence("(true)")
        }
    }

    @Nested
    @DisplayName("FalseLiteral")
    inner class FalseLiteralTest {
        @Test
        fun `Parse false`() {
            val result = parseExpression(FalseLiteral.ParseFalseLiteral, "false")
            result.verifyPrecedence("(false)")
        }
    }

    @Nested
    @DisplayName("NilLiteral")
    inner class NilLiteralTest {
        @Test
        fun `Parse nil`() {
            val result = parseExpression(NilLiteral.ParseNilLiteral, "nil")
            result.verifyPrecedence("(nil)")
        }
    }
}