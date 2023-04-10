package io.hamal.module.worker.script.lexer

import io.hamal.lib.meta.exception.IllegalStateException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class LexerTest {

    @Nested
    @DisplayName("DefaultImpl")
    inner class DefaultImplTest {

        @Nested
        @DisplayName("isAtEnd()")
        inner class IsAtEndTest {
            @Test
            fun `Advances until reaches end`() {
                val testInstance = Lexer.DefaultImpl("num")
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertFalse(testInstance.isAtEnd())
                testInstance.advance()
                assertTrue(testInstance.isAtEnd())
            }
        }

        @Nested
        @DisplayName("peek()")
        inner class PeekTest {
            @Test
            fun `Returns the character of the current position`() {
                val testInstance = Lexer.DefaultImpl("hamal")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                assertThat(testInstance.peek(), equalTo('a'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = Lexer.DefaultImpl("h")
                assertThat(testInstance.peek(), equalTo('h'))
                testInstance.advance()
                val exception = assertThrows<IllegalStateException> {
                    testInstance.peek()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("peekNext()")
        inner class PeekNextTest {
            @Test
            fun `Return the next character`() {
                val testInstance = Lexer.DefaultImpl("hamal")
                assertThat(testInstance.peekNext(), equalTo('a'))
                testInstance.advance()
                assertThat(testInstance.peekNext(), equalTo('m'))
            }

            @Test
            fun `Throws exception if there is nothing to peek anymore`() {
                val testInstance = Lexer.DefaultImpl("42")
                assertThat(testInstance.peekNext(), equalTo('2'))
                testInstance.advance()

                val exception = assertThrows<IllegalStateException> {
                    testInstance.peekNext()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("advance()")
        inner class AdvanceTest() {
            @Test
            fun `Advances read position and fills buffer`() {
                val testInstance = Lexer.DefaultImpl("num = 42")
                testInstance.advance().assert(1, 1, 1, "n")
                testInstance.advance().assert(2, 1, 2, "nu")
                testInstance.advance().assert(3, 1, 3, "num")
            }

            @Test
            fun `Throws exception if there is nothing to read anymore`() {
                val testInstance = Lexer.DefaultImpl("n")
                testInstance.advance().assert(1, 1, 1, "n")

                val exception = assertThrows<IllegalStateException> {
                    testInstance.advance()
                }
                assertThat(exception.message, equalTo("Can not read after end of code"))
            }
        }

        @Nested
        @DisplayName("advanceUntilWhitespace()")
        inner class AdvanceUntilWhitespaceTest {
            @Test
            fun `Advances until hits whitespace`() {
                val testInstance = Lexer.DefaultImpl("hamal rockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits new line`() {
                val testInstance = Lexer.DefaultImpl("hamal\nrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits tabulator`() {
                val testInstance = Lexer.DefaultImpl("hamal\trockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }

            @Test
            fun `Advances until hits carriage return`() {
                val testInstance = Lexer.DefaultImpl("hamal\rrockz")
                testInstance.advanceUntilWhitespace()
                testInstance.assert(5, 1, 5, "hamal")
            }
        }

        @Nested
        @DisplayName("skipWhitespace()")
        inner class SkipWhitespaceTest {
            @Test
            fun `Does nothing if not a whitespace`() {
                val testInstance = Lexer.DefaultImpl("hamal")
                testInstance.skipWhitespace().assert(0, 1, 0, "")
            }

            @Test
            fun `Skips comment`() {
                val testInstance = Lexer.DefaultImpl(
                    """
                    --- some comment on how awesome hamal is
                    invoke_awesomeness()
                """.trimIndent()
                )
                testInstance.skipWhitespace().assert(41, 2, 1, "")
            }

            @Test
            fun `Skips whitespaces`() {
                val testInstance = Lexer.DefaultImpl(
                    """    content"""
                )
                testInstance.skipWhitespace().assert(4, 1, 4, "")
            }

            @Test
            fun `Skips line breaks`() {
                val testInstance = Lexer.DefaultImpl(
                    "\n\n\ncontent"
                )
                testInstance.skipWhitespace().assert(3, 4, 1, "")
            }

            @Test
            fun `Skips tabulators breaks`() {
                val testInstance = Lexer.DefaultImpl(
                    "\t\t\tcontent"
                )
                testInstance.skipWhitespace().assert(3, 1, 3, "")
            }

            @Test
            fun `Skips carriage return breaks`() {
                val testInstance = Lexer.DefaultImpl(
                    "\r\rcontent"
                )
                testInstance.skipWhitespace().assert(2, 1, 2, "")
            }
        }

        private fun Lexer.DefaultImpl.assert(index: Int, line: Int, linePosition: Int, buffer: String) {
            assertThat("index is not $index", this.index, equalTo(index))
            assertThat("line is not $line", this.line, equalTo(line))
            assertThat("line position is not $linePosition", this.linePosition, equalTo(linePosition))
            assertThat("buffer is not $buffer", this.buffer.toString(), equalTo(buffer))
        }
    }
}