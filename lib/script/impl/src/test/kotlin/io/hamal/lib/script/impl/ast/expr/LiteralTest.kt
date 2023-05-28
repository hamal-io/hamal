package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.value.DepNumberValue
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
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
                    tokens.wereConsumed()
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
                    tokens.wereConsumed()
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
                    tokens.wereConsumed()
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
                    tokens.wereConsumed()
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
                    tokens.wereConsumed()
                }
            }
        }
    }
}