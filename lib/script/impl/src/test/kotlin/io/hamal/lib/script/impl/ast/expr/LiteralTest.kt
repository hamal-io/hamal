package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.somePosition
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
                    NumberLiteral(somePosition, 2810),
                    NumberLiteral(somePosition, 2810)
                )

                assertEquals(
                    NumberLiteral(somePosition, 2810),
                    NumberLiteral(anotherPosition, 2810)
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    NumberLiteral(somePosition, 2810),
                    NumberLiteral(somePosition, 1506)
                )
            }
        }

        @Nested
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    NumberLiteral(somePosition, 2810).hashCode(),
                    NumberLiteral(somePosition, 2810).hashCode()
                )

                assertEquals(
                    NumberLiteral(somePosition, 2810).hashCode(),
                    NumberLiteral(anotherPosition, 2810).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    NumberLiteral(somePosition, 2810).hashCode(),
                    NumberLiteral(somePosition, 1506).hashCode()
                )
            }
        }

        @Nested
        inner class ParseTest {
            @Test
            fun number() {
                runLiteralTest(NumberLiteral.Parse, "28.10") { result, tokens ->
                    assertThat(result, equalTo(NumberLiteral(somePosition, NumberValue("28.10"))))
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
                    StringLiteral(somePosition, "H4M41"),
                    StringLiteral(somePosition, "H4M41")
                )
                assertEquals(
                    StringLiteral(somePosition, "H4M41"),
                    StringLiteral(anotherPosition, "H4M41")
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    StringLiteral(somePosition, "H4M41"),
                    StringLiteral(somePosition, "CRAPPY_ENGINE")
                )
            }
        }

        @Nested
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    StringLiteral(somePosition, "H4M41").hashCode(),
                    StringLiteral(somePosition, "H4M41").hashCode()
                )
                assertEquals(
                    StringLiteral(somePosition, "H4M41").hashCode(),
                    StringLiteral(anotherPosition, "H4M41").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    StringLiteral(somePosition, "H4M41").hashCode(),
                    StringLiteral(anotherPosition, "CRAPPY_ENGINE").hashCode()
                )
            }
        }

        @Nested
        inner class ParseTest {
            @Test
            fun string() {
                runLiteralTest(StringLiteral.Parse, "'hello hamal'") { result, tokens ->
                    assertThat(result, equalTo(StringLiteral(somePosition, "hello hamal")))
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
                    assertThat(result, equalTo(TrueLiteral(somePosition)))
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
                    assertThat(result, equalTo(FalseLiteral(somePosition)))
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
            fun nil() {
                runLiteralTest(NilLiteral.Parse, "nil") { result, tokens ->
                    assertThat(result, equalTo(NilLiteral(somePosition)))
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
                    CodeLiteral(somePosition, "hamal.rocks()"),
                    CodeLiteral(somePosition, "hamal.rocks()")
                )

                assertEquals(
                    CodeLiteral(somePosition, "hamal.rocks()"),
                    CodeLiteral(anotherPosition, "hamal.rocks()")
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    CodeLiteral(somePosition, "hamal.rocks()"),
                    CodeLiteral(somePosition, "rocks.hamal()")
                )
            }
        }

        @Nested
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    CodeLiteral(somePosition, "hamal.rocks()").hashCode(),
                    CodeLiteral(somePosition, "hamal.rocks()").hashCode()
                )

                assertEquals(
                    CodeLiteral(somePosition, "hamal.rocks()").hashCode(),
                    CodeLiteral(anotherPosition, "hamal.rocks()").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    CodeLiteral(somePosition, "hamal.rocks()").hashCode(),
                    CodeLiteral(somePosition, "rocks.hamal()").hashCode()
                )
            }
        }

        @Nested
        inner class ParseTest {
            @Test
            fun code() {
                runLiteralTest(CodeLiteral.Parse, "[[hamal.rocks()]]") { result, tokens ->
                    assertThat(result, equalTo(CodeLiteral(somePosition, "hamal.rocks()")))
                    tokens.consumed()
                }
            }
        }
    }
}