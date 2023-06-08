package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.value.DepNumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class LiteralTest : AbstractExpressionTest() {
    @Nested
    inner class NumberTest {
        @Nested
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
        inner class ParseTest {
            @Test
            fun number() {
                runLiteralTest(NumberLiteral.Parse, "28.10") { result, tokens ->
                    assertThat(result, equalTo(NumberLiteral(DepNumberValue("28.10"))))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class StringTest {
        @Nested
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
        inner class ParseTest {
            @Test
            fun string() {
                runLiteralTest(StringLiteral.Parse, "'hello hamal'") { result, tokens ->
                    assertThat(result, equalTo(StringLiteral("hello hamal")))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class TrueTest {
        @Nested
        inner class ParseTest {
            @Test
            fun `true`() {
                runLiteralTest(TrueLiteral.Parse, "true") { result, tokens ->
                    assertThat(result, equalTo(TrueLiteral))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class FalseTest {
        @Nested
        inner class ParseTest {
            @Test
            fun `false`() {
                runLiteralTest(FalseLiteral.Parse, "false") { result, tokens ->
                    assertThat(result, equalTo(FalseLiteral))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class NilTest {
        @Nested
        inner class ParseTest {
            @Test
            fun `nil`() {
                runLiteralTest(NilLiteral.Parse, "nil") { result, tokens ->
                    assertThat(result, equalTo(NilLiteral))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class CodeTest {
        @Nested
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    CodeLiteral("hamal.rocks()"),
                    CodeLiteral("hamal.rocks()")
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    CodeLiteral("hamal.rocks()"),
                    CodeLiteral("rocks.hamal()")
                )
            }
        }

        @Nested
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    CodeLiteral("hamal.rocks()").hashCode(),
                    CodeLiteral("hamal.rocks()").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    CodeLiteral("hamal.rocks()").hashCode(),
                    CodeLiteral("rocks.hamal()").hashCode()
                )
            }
        }

        @Nested
        inner class ParseTest {
            @Test
            fun code() {
                runLiteralTest(CodeLiteral.Parse, "<[hamal.rocks()]>") { result, tokens ->
                    assertThat(result, equalTo(CodeLiteral("hamal.rocks()")))
                    tokens.consumed()
                }
            }
        }
    }
}