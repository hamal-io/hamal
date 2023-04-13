package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractAstTest() {
    @Test
    fun fail() {
        throw Error("implement test for equals and hashcode")
    }

    @Nested
    @DisplayName("Number")
    inner class NumberTest {
        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `Parse number token`() {
                val result = parseSimpleLiteralExpression(Number.Parse, "28.10")
                result.verifyPrecedence("28.10")
            }
        }
    }

    @Nested
    @DisplayName("String")
    inner class StringTest {
        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `Parses string token`() {
                val result = parseSimpleLiteralExpression(String.Parse, "'hello hamal'")
                result.verifyPrecedence("'hello hamal'")
            }
        }
    }

    @Nested
    @DisplayName("True")
    inner class TrueTest {
        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `Parse true`() {
                val result = parseSimpleLiteralExpression(True.Parse, "true")
                result.verifyPrecedence("true")
            }
        }
    }

    @Nested
    @DisplayName("False")
    inner class FalseTest {
        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `Parse false`() {
                val result = parseSimpleLiteralExpression(False.Parse, "false")
                result.verifyPrecedence("false")
            }
        }
    }

    @Nested
    @DisplayName("Nil")
    inner class NilTest {
        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `Parse nil`() {
                val result = parseSimpleLiteralExpression(Nil.ParseNilLiteral, "nil")
                result.verifyPrecedence("nil")
            }
        }
    }
}