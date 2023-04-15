package io.hamal.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.script.ast.expr.*
import io.hamal.script.token.Token.Type
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
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    Number(2810),
                    Number(2810)
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    Number(2810),
                    Number(1506)
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    Number(2810).hashCode(),
                    Number(2810).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    Number(2810).hashCode(),
                    Number(1506).hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun number() {
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
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    String("H4M41"),
                    String("H4M41")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    String("H4M41"),
                    String("CRAPPY_WORKFLOW_ENGINE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    String("H4M41").hashCode(),
                    String("H4M41").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    String("H4M41").hashCode(),
                    String("CRAPPY_WORKFLOW_ENGINE").hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun string() {
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
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    True(),
                    True()
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    True(),
                    False()
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    True().hashCode(),
                    True().hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    True().hashCode(),
                    False().hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `true`() {
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
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    False(),
                    False()
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    False(),
                    True()
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    False().hashCode(),
                    False().hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    False().hashCode(),
                    True().hashCode()
                )
            }
        }

        @Nested
        @DisplayName("Parse()")
        inner class ParseTest {
            @Test
            fun `false`() {
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
            fun `nil`() {
                runLiteralTest(Nil.Parse, "nil") { result, tokens ->
                    assertThat(result, equalTo(Nil()))
                    tokens.inOrder(Type.Nil, Type.Eof)
                }
            }
        }
    }
}