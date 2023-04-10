package io.hamal.module.worker.script.lexer

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LexerUtilTest {

    @Nested
    @DisplayName("isDigit()")
    inner class IsDigitTest {
        @Test
        fun `Is digit`() {
            for (i in 0..9) {
                assertTrue(LexerUtil.isDigit(Char(i + 48)))
            }
        }

        @Test
        fun `Not digit`() {
            for (i in 0..47) {
                assertFalse(LexerUtil.isDigit(Char(i)))
            }

            for (i in 58..254) {
                assertFalse(LexerUtil.isDigit(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isAlpha()")
    inner class IsAlphaTest {
        @Test
        fun `Is alpha`() {
            for (i in 65..90) {
                assertTrue(LexerUtil.isAlpha(Char(i)))
            }
            for (i in 97..122) {
                assertTrue(LexerUtil.isAlpha(Char(i)))
            }
        }

        @Test
        fun `Not alpha`() {
            for (i in 0..64) {
                assertFalse(LexerUtil.isAlpha(Char(i)))
            }
            for (i in 91..96) {
                assertFalse(LexerUtil.isAlpha(Char(i)))
            }
            for (i in 123..255) {
                assertFalse(LexerUtil.isAlpha(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isUnderscore()")
    inner class IsUnderscoreTest {
        @Test
        fun `Is underscore`() {
            assertTrue(LexerUtil.isUnderscore('_'))
        }

        @Test
        fun `Not underscore`() {
            for (i in 0..94) {
                assertFalse(LexerUtil.isUnderscore(Char(i)))
            }
            for (i in 96..255) {
                assertFalse(LexerUtil.isUnderscore(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isWhitespace()")
    inner class IsWhitespaceTest {
        @Test
        fun `Is whitespace`() {
            assertTrue(LexerUtil.isWhitespace(' '))
            assertTrue(LexerUtil.isWhitespace('\t'))
            assertTrue(LexerUtil.isWhitespace('\n'))
            assertTrue(LexerUtil.isWhitespace('\r'))
        }

        @Test
        fun `Not whitespace`() {
            for (i in 0..255) {
                if (i in intArrayOf(9, 10, 13, 32)) {
                    continue
                }
                assertFalse(LexerUtil.isWhitespace(Char(i)))
            }
        }
    }

    @Nested
    @DisplayName("isHexChar()")
    inner class IsHexCharTest {
        @Test
        fun `Is hex char`() {
            for (i in 48..57) {
                assertTrue(LexerUtil.isHexChar(Char(i)))
            }
            for (i in 65..70) {
                assertTrue(LexerUtil.isHexChar(Char(i)))
            }
            for (i in 97..102) {
                assertTrue(LexerUtil.isHexChar(Char(i)))
            }
        }

        @Test
        fun `Not hex char`() {
            for (i in 0..47) {
                assertFalse(LexerUtil.isHexChar(Char(i)))
            }
            for (i in 58..64) {
                assertFalse(LexerUtil.isHexChar(Char(i)))
            }
            for (i in 71..96) {
                assertFalse(LexerUtil.isHexChar(Char(i)))
            }
            for (i in 103..255) {
                assertFalse(LexerUtil.isHexChar(Char(i)))
            }
        }
    }

}