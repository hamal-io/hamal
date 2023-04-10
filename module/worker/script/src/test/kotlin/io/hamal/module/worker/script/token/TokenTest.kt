package io.hamal.module.worker.script.token

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class TokenTest {

    @Nested
    @DisplayName("TokenLine")
    inner class TokenLineTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    TokenLine(10),
                    TokenLine(10)
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    TokenLine(10),
                    TokenLine(28)
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TokenLine(10).hashCode(),
                    TokenLine(10).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TokenLine(10).hashCode(),
                    TokenLine(28).hashCode()
                )
            }
        }
    }

    @Nested
    @DisplayName("TokenPosition")
    inner class TokenPositionTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    TokenPosition(10),
                    TokenPosition(10)
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    TokenPosition(10),
                    TokenPosition(28)
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TokenPosition(10).hashCode(),
                    TokenPosition(10).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TokenPosition(10).hashCode(),
                    TokenPosition(28).hashCode()
                )
            }
        }
    }

    @Nested
    @DisplayName("TokenLiteral")
    inner class TokenLiteralTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    TokenLiteral("h4m4l"),
                    TokenLiteral("h4m4l")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    TokenLiteral("h4m4l"),
                    TokenLiteral("rockz")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TokenLiteral("h4m4l").hashCode(),
                    TokenLiteral("h4m4l").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TokenLiteral("h4m4l").hashCode(),
                    TokenLiteral("rockz").hashCode()
                )
            }
        }
    }

}