package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.common.math.Decimal
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.token.Token.Type
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("Number")
    inner class NumberTest {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    NumberLiteral(2810),
                    NumberLiteral(2810)
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    NumberLiteral(2810),
                    NumberLiteral(1506)
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    NumberLiteral(2810).hashCode(),
                    NumberLiteral(2810).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    NumberLiteral(2810).hashCode(),
                    NumberLiteral(1506).hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun number() {
                runLiteralTest(NumberLiteral.Parse, "28.10") { result, tokens ->
                    assertThat(result, equalTo(NumberLiteral(Decimal("28.10"))))
                    tokens.inOrder(Type.Number, Type.Eof)
                }
            }
        }
    }

    @Nested
    @DisplayName("String")
    inner class StringTest {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    StringLiteral("H4M41"),
                    StringLiteral("H4M41")
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    StringLiteral("H4M41"),
                    StringLiteral("CRAPPY_WORKJOB_ENGINE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    StringLiteral("H4M41").hashCode(),
                    StringLiteral("H4M41").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    StringLiteral("H4M41").hashCode(),
                    StringLiteral("CRAPPY_WORKJOB_ENGINE").hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun string() {
                runLiteralTest(StringLiteral.Parse, "'hello hamal'") { result, tokens ->
                    assertThat(result, equalTo(StringLiteral("hello hamal")))
                    tokens.inOrder(Type.String, Type.Eof)
                }
            }
        }
    }

    @Nested
    @DisplayName("True")
    inner class TrueTest {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    TrueLiteral(),
                    TrueLiteral()
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    TrueLiteral(),
                    FalseLiteral()
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TrueLiteral().hashCode(),
                    TrueLiteral().hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TrueLiteral().hashCode(),
                    FalseLiteral().hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `true`() {
                runLiteralTest(TrueLiteral.Parse, "true") { result, tokens ->
                    assertThat(result, equalTo(TrueLiteral()))
                    tokens.inOrder(Type.True, Type.Eof)
                }
            }
        }
    }

    @Nested
    @DisplayName("False")
    inner class FalseTest {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    FalseLiteral(),
                    FalseLiteral()
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    FalseLiteral(),
                    TrueLiteral()
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    FalseLiteral().hashCode(),
                    FalseLiteral().hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    FalseLiteral().hashCode(),
                    TrueLiteral().hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `false`() {
                runLiteralTest(FalseLiteral.Parse, "false") { result, tokens ->
                    assertThat(result, equalTo(FalseLiteral()))
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
            fun `nil`() {
                runLiteralTest(NilLiteral.Parse, "nil") { result, tokens ->
                    assertThat(result, equalTo(NilLiteral()))
                    tokens.inOrder(Type.Nil, Type.Eof)
                }
            }
        }
    }
}