package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.impl.ast.expr.*
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
                    assertThat(result, equalTo(NumberLiteral(NumberValue("28.10"))))
                    tokens.wereConsumed()
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
                    StringLiteral("CRAPPY_ENGINE")
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
                    StringLiteral("CRAPPY_ENGINE").hashCode()
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
                    tokens.wereConsumed()
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
            fun `true`() {
                runLiteralTest(TrueLiteral.Parse, "true") { result, tokens ->
                    assertThat(result, equalTo(TrueLiteral))
                    tokens.wereConsumed()
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
            fun `false`() {
                runLiteralTest(FalseLiteral.Parse, "false") { result, tokens ->
                    assertThat(result, equalTo(FalseLiteral))
                    tokens.wereConsumed()
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
                    assertThat(result, equalTo(NilLiteral))
                    tokens.wereConsumed()
                }
            }
        }
    }
}