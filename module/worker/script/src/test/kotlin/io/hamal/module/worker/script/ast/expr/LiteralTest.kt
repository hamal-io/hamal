package io.hamal.module.worker.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.module.worker.script.token.Token.Type
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractExpressionTest() {
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
                runLiteralTest(Number.Parse, "28.10") { result, tokens ->
                    assertThat(result, equalTo(Number(Decimal("28.10"))))
                    tokens.inOrder(Type.Number, Type.Eof)
                }
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
                runLiteralTest(String.Parse, "'hello hamal'") { result, tokens ->
                    assertThat(result, equalTo(String("hello hamal")))
                    tokens.inOrder(Type.String, Type.Eof)
                }
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
                runLiteralTest(True.Parse, "true") { result, tokens ->
                    assertThat(result, equalTo(True()))
                    tokens.inOrder(Type.True, Type.Eof)
                }
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
                runLiteralTest(False.Parse, "false") { result, tokens ->
                    assertThat(result, equalTo(False()))
                    tokens.inOrder(Type.False, Type.Eof)
                }
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
                runLiteralTest(Nil.Parse, "nil") { result, tokens ->
                    assertThat(result, equalTo(Nil()))
                    tokens.inOrder(Type.Nil, Type.Eof)
                }
            }
        }
    }
}