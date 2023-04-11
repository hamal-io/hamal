package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractAstTest() {

    @Nested
    @DisplayName("TrueLiteral.Parser")
    inner class TrueLiteralParserTest {
        @Test
        fun `Parse true`() {
            val result = parse(TrueLiteral.Parser, "true")
            result.verifyPrecedence("(true)")
        }
    }

    @Nested
    @DisplayName("FalseLiteral.Parser")
    inner class FalseLiteralParserTest {
        @Test
        fun `Parse false`() {
            val result = parse(FalseLiteral.Parser, "false")
            result.verifyPrecedence("(false)")
        }
    }

    @Nested
    @DisplayName("NilLiteral.Parser")
    inner class NilLiteralParserTest {
        @Test
        fun `Parse nil`() {
            val result = parse(NilLiteral.Parser, "nil")
            result.verifyPrecedence("(nil)")
        }
    }
}