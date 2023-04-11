package io.hamal.module.worker.script.ast.literal

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralNilTest : AbstractAstTest() {

    @Nested
    @DisplayName("ParseLiteralNil")
    inner class ParseLiteralNilTest {
        @Test
        fun `Parse nil`() {
            val result = parse(ParseLiteralNil, "nil")
            assertNilLiteral(result)
            assertPosition(result, 1, 1)
            result.verifyPrecedence("(nil)")
        }
    }
}