package io.hamal.lib.common.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TokenizerUtilsTest {

    @Nested
    @DisplayName("isDigit()")
    inner class IsDigitTest {
        @Test
        fun `Is digit`() {
            for (i in 0..9) {
                assertTrue(TokenizerUtils.isDigit(Char(i + 48)))
            }
        }

        @Test
        fun `Not digit`() {
            for (i in 0..47) {
                assertFalse(TokenizerUtils.isDigit(Char(i)))
            }

            for (i in 58..254) {
                assertFalse(TokenizerUtils.isDigit(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isAlpha()")
    inner class IsAlphaTest {
        @Test
        fun `Is alpha`() {
            for (i in 65..90) {
                assertTrue(TokenizerUtils.isAlpha(Char(i)))
            }
            for (i in 97..122) {
                assertTrue(TokenizerUtils.isAlpha(Char(i)))
            }
        }

        @Test
        fun `Not alpha`() {
            for (i in 0..64) {
                assertFalse(TokenizerUtils.isAlpha(Char(i)))
            }
            for (i in 91..96) {
                assertFalse(TokenizerUtils.isAlpha(Char(i)))
            }
            for (i in 123..255) {
                assertFalse(TokenizerUtils.isAlpha(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isUnderscore()")
    inner class IsUnderscoreTest {
        @Test
        fun `Is underscore`() {
            assertTrue(TokenizerUtils.isUnderscore('_'))
        }

        @Test
        fun `Not underscore`() {
            for (i in 0..94) {
                assertFalse(TokenizerUtils.isUnderscore(Char(i)))
            }
            for (i in 96..255) {
                assertFalse(TokenizerUtils.isUnderscore(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isQuote()")
    inner class IsQuoteTest {
        @Test
        fun `Is quote`() {
            assertTrue(TokenizerUtils.isQuote('\''))
        }

        @Test
        fun `Not quote`() {
            for (i in 0..38) {
                assertFalse(TokenizerUtils.isQuote(Char(i)))
            }
            for (i in 50..255) {
                assertFalse(TokenizerUtils.isQuote(Char(i)))
            }
        }
    }


    @Nested
    @DisplayName("isWhitespace()")
    inner class IsWhitespaceTest {
        @Test
        fun `Is whitespace`() {
            assertTrue(TokenizerUtils.isWhitespace(' '))
            assertTrue(TokenizerUtils.isWhitespace('\t'))
            assertTrue(TokenizerUtils.isWhitespace('\n'))
            assertTrue(TokenizerUtils.isWhitespace('\r'))
        }

        @Test
        fun `Not whitespace`() {
            for (i in 0..255) {
                if (i in intArrayOf(9, 10, 13, 32)) {
                    continue
                }
                assertFalse(TokenizerUtils.isWhitespace(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isHexChar()")
    inner class IsHexCharTest {
        @Test
        fun `Is hex char`() {
            for (i in 48..57) {
                assertTrue(TokenizerUtils.isHexChar(Char(i)))
            }
            for (i in 65..70) {
                assertTrue(TokenizerUtils.isHexChar(Char(i)))
            }
            for (i in 97..102) {
                assertTrue(TokenizerUtils.isHexChar(Char(i)))
            }
        }

        @Test
        fun `Not hex char`() {
            for (i in 0..47) {
                assertFalse(TokenizerUtils.isHexChar(Char(i)))
            }
            for (i in 58..64) {
                assertFalse(TokenizerUtils.isHexChar(Char(i)))
            }
            for (i in 71..96) {
                assertFalse(TokenizerUtils.isHexChar(Char(i)))
            }
            for (i in 103..255) {
                assertFalse(TokenizerUtils.isHexChar(Char(i)))
            }
        }
    }

}